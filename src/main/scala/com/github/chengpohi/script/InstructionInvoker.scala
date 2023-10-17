package com.github.chengpohi.script

import com.github.chengpohi.context.{AuthInfo, HostInfo}
import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.edql.parser.{EDQLParserFactory, EDQLPsiInterceptor}
import org.apache.commons.lang3.StringUtils

import java.io.{BufferedReader, InputStreamReader}
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.net.{URI, URL}
import java.util.stream.Collectors
import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Success}

trait InstructionInvoker {
  val factory: EDQLParserFactory
  lazy val parser = new EDQLPsiInterceptor(factory)
  private val httpClient: HttpClient = HttpClient.newHttpClient()
  val libs: Seq[URL]

  import parser._

  def invokeInstruction(invokeIns: Seq[Instruction2],
                        scriptContextIns: Seq[Instruction2],
                        runContext: EDQLRunContext): EDQLRunResult = {

    val endpointBind = scriptContextIns.find(_.isInstanceOf[EndpointBindInstruction])
      .map(i => i.asInstanceOf[EndpointBindInstruction])
    if (endpointBind.isEmpty && runContext.hostInfo == null) {
      return EDQLRunResult(Failure(new RuntimeException("should configure host")))
    }

    val (functions, context) = this.buildContext(scriptContextIns, endpointBind, runContext)

    val invokeResult = runInstructions(functions, context, invokeIns)
    EDQLRunResult(invokeResult.map {
      case j: JsonCollection.Str => j.raw
      case j: JsonCollection.Var => j.realValue map {
        case t: JsonCollection.Str => t.raw
        case t => t.toJson
      } getOrElse ""
      case a => a.toJson
    }.filter(_.nonEmpty), runContext, context)
  }

  private def buildContext(cIns: Seq[Instruction2],
                           endpoint: Option[EndpointBindInstruction],
                           runContext: EDQLRunContext) = {
    val importIns = parseImports(cIns ++ libs.map(ImportInstruction), runContext.runDir)

    val invokeIns = cIns ++ importIns

    val duplicateVariables = invokeIns.filter(_.isInstanceOf[VariableInstruction])
      .map(i => i.asInstanceOf[VariableInstruction])
      .groupBy(_.variableName)
      .filter(_._2.size >= 2)

    if (duplicateVariables.nonEmpty) {
      throw new RuntimeException("duplicate variable: " + duplicateVariables.mkString(","))
    }

    val vars =
      invokeIns.filter(_.isInstanceOf[VariableInstruction])
        .map(i => i.asInstanceOf[VariableInstruction])
        .map(i => i.variableName -> i.value).toMap

    val duplicateFunctions = invokeIns.filter(_.isInstanceOf[FunctionInstruction])
      .map(i => i.asInstanceOf[FunctionInstruction])
      .map(i => i.funcName + i.variableNames.size -> i)
      .groupBy(_._1).filter(_._2.size >= 2)

    if (duplicateFunctions.nonEmpty) {
      throw new RuntimeException("duplicate function: " + duplicateFunctions.mkString(","))
    }

    val globalFunctions =
      invokeIns.filter(_.isInstanceOf[FunctionInstruction])
        .map(i => i.asInstanceOf[FunctionInstruction])
        .map(i => i.funcName + i.variableNames.size -> i)
        .toMap ++ systemFunction

    val globalVars = vars.map(i => i._1 -> i._2) + ("CONTEXT_PATH" -> JsonCollection.Str(runContext.runDir))
    val hostInfo = buildHostInfo(runContext, endpoint, invokeIns)
    val context = ScriptContext(hostInfo, globalVars)

    evalFunParams(globalFunctions, context, vars)
    (globalFunctions, context)
  }

  private def buildHostInfo(runContext: EDQLRunContext, endPoint: Option[EndpointBindInstruction], invokeIns: Seq[Instruction2]): HostInfo = {
    if (runContext.hostInfo != null) {
      return runContext.hostInfo
    }

    val authorization =
      invokeIns.find(_.isInstanceOf[AuthorizationBindInstruction])
        .map(i => i.asInstanceOf[AuthorizationBindInstruction])
        .map(i => i.auth).orNull

    val username =
      invokeIns.find(_.isInstanceOf[UsernameBindInstruction])
        .map(i => i.asInstanceOf[UsernameBindInstruction])
        .map(i => i.username).orNull

    val password =
      invokeIns.find(_.isInstanceOf[PasswordBindInstruction])
        .map(i => i.asInstanceOf[PasswordBindInstruction])
        .map(i => i.password).orNull
    val apikeyId =
      invokeIns.find(_.isInstanceOf[ApiKeyIdBindInstruction])
        .map(i => i.asInstanceOf[ApiKeyIdBindInstruction])
        .map(i => i.apikeyId).orNull
    val apikeySecret =
      invokeIns.find(_.isInstanceOf[ApiKeySecretBindInstruction])
        .map(i => i.asInstanceOf[ApiKeySecretBindInstruction])
        .map(i => i.apiSecret).orNull
    val apiSessionToken =
      invokeIns.find(_.isInstanceOf[ApiSessionTokenBindInstruction])
        .map(i => i.asInstanceOf[ApiSessionTokenBindInstruction])
        .map(i => i.apiSessionToken).orNull
    val awsRegion =
      invokeIns.find(_.isInstanceOf[AWSRegionBindInstruction])
        .map(i => i.asInstanceOf[AWSRegionBindInstruction])
        .map(i => i.awsRegion).orNull

    val timeout =
      invokeIns.find(_.isInstanceOf[TimeoutInstruction])
        .map(i => i.asInstanceOf[TimeoutInstruction])
        .map(i => i.timeout).getOrElse(5000)
    val authInfo = AuthInfo(authorization, username, password, apikeyId, apikeySecret, apiSessionToken, awsRegion, null)
    HostInfo(endPoint.get.endpoint, URI.create(endPoint.get.endpoint), timeout, endPoint.get.kibanaProxy, Some(authInfo))
  }

  private def parseImports(cIns: Seq[Instruction2], runDir: String): Seq[Instruction2] = {
    val imports =
      cIns.filter(_.isInstanceOf[ImportInstruction])
        .map(i => i.asInstanceOf[ImportInstruction])

    if (imports.isEmpty) {
      return Seq()
    }

    val importStr = imports.map(i => {
      handleImport(runDir, httpClient, i)
    }).mkString("").trim

    val importIns = parser.parse(importStr) match {
      case Success(ins) => ins
      case Failure(f) => throw new RuntimeException("import parse failed" + f.getMessage, f)
    }
    importIns ++ parseImports(importIns, runDir).filter(i => i.isInstanceOf[FunctionInstruction] || i.isInstanceOf[VariableInstruction])
  }

  private def handleImport(runDir: String, client: HttpClient, i: ImportInstruction): String = {
    val imp = i.imp
    val content = readFile(runDir, imp)
    if (!StringUtils.isEmpty(content)) {
      return content
    }
    val body = readFromWeb(client, imp)

    if (!StringUtils.isEmpty(body)) {
      return body
    }

    throw new RuntimeException("could not found content from " + imp)
  }

  private def readFromWeb(client: HttpClient, imp: URL): String = {
    try {
      val httpRequest = HttpRequest.newBuilder()
        .uri(imp.toURI)
        .GET()
        .build();
      val response = client.send(httpRequest, BodyHandlers.ofString())
      response.body();
    } catch {
      case _: Throwable => ""
    }
  }

  private def readFile(runDir: String, url: URL): String = {
    try {
      val reader = new BufferedReader(new InputStreamReader(url.openStream()));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()))
    } catch {
      case _: Throwable => ""
    }

    try {
      Source.fromInputStream(url.openStream()).getLines().mkString(System.lineSeparator())
    } catch {
      case _: Throwable => ""
    }
  }

  private def evalFunParams(globalFunctions: Map[String, FunctionInstruction],
                            context: ScriptContext,
                            parms: Map[String, JsonCollection.Val],
                            funName: Option[String] = None
                           ) = {
    parms.filter(_._2.isInstanceOf[JsonCollection.Fun])
      .foreach(i => {
        val fParam = i._2.asInstanceOf[JsonCollection.Fun]
        val value = invokeFunction(globalFunctions, context, FunctionInvokeInstruction(fParam.value._1, fParam.value._2), funName).lastOption
        fParam.realValue = value
        context.variables.put(i._1, value.getOrElse(JsonCollection.Null))
      })

    val arithes = parms.filter(_._2.isInstanceOf[JsonCollection.ArithTree]).map(i => i._2.asInstanceOf[JsonCollection.ArithTree])
    arithes.foreach(a => {
      evalArith(globalFunctions, context, a, funName)
    })

    val vals = parms.filter(!_._2.isInstanceOf[JsonCollection.Fun]).map(i => {
      if (i._2.isInstanceOf[JsonCollection.Var]) {
        mapRealValue(context.variables, i._2, funName)
      }
      i._1 -> i._2
    })
    context.variables.addAll(vals)
  }

  def extractCollection(iterVariable: JsonCollection.Val): JsonCollection.Arr = {
    iterVariable match {
      case c: JsonCollection.Arr => c
      case t: JsonCollection.Var if t.realValue.get.isInstanceOf[JsonCollection.Arr] =>
        t.realValue.get.asInstanceOf[JsonCollection.Arr]
      case _ =>
        throw new RuntimeException("iter variable not a collection")
    }
  }


  def iterCollection(functions: Map[String, FunctionInstruction],
                     context: ScriptContext,
                     r: ForInstruction): Seq[JsonCollection.Val] = {
    val cachedVariables = context.variables

    val iterVariable = r.iterVariable

    val instructions = extractCollection(iterVariable).value.flatMap(i => {
      context.variables = mutable.Map[String, JsonCollection.Val](cachedVariables.toSeq: _*)
      context.variables.put(r.tempVariable, i)
      runInstructions(functions, context, r.instructions)
    })

    context.variables = cachedVariables
    instructions
  }


  def invokeFunction(functions: Map[String, FunctionInstruction],
                     context: ScriptContext,
                     invoke: FunctionInvokeInstruction,
                     parentFunName: Option[String] = None): Seq[JsonCollection.Val] = {
    val cachedVariables = context.variables
    val values = invoke.vals

    val foundFunction = functions.get(invoke.funcName + values.size)
    if (foundFunction.isEmpty) {
      throw new RuntimeException("Could not found method: " + invoke.funcName + " with parameters "
        + values.size)
    }

    val fun = foundFunction.get
    clearContextBeforeInvoke(fun.instructions)
    val funName = fun.funcName + "_" + fun.variableNames.size
    val funParams = fun.variableNames.map(i => funName + "$" + i).zip(values).toMap

    funParams.filter(_._2.isInstanceOf[JsonCollection.Var]).foreach(i => {
      i._2.asInstanceOf[JsonCollection.Var].realValue = None
    })

    evalFunParams(functions, context, funParams, parentFunName.orElse(Some(funName)))

    val instructions = fun.instructions

    val funcBodyVars = instructions.filter(_.isInstanceOf[VariableInstruction])
      .map(i => i.asInstanceOf[VariableInstruction])
      .map(i => {
        funName + "$" + i.variableName -> i.value
      }).toMap

    evalFunParams(functions, context, funcBodyVars, Some(funName))

    val response = runInstructions(functions, context, instructions, Some(funName))

    context.variables = cachedVariables

    if (invoke.map.isEmpty) {
      return response
    }
    response match {
      case Seq(r) =>
        r match {
          case a: JsonCollection.Arr =>
            val m = invoke.map.get.copy(arr = a)
            invokeMapIter(functions, context, m, parentFunName)
          case _ => response
        }
      case _ => response
    }
  }

  private def clearContextBeforeInvoke(instructions: Seq[Instruction2]) = {
    instructions.foreach(fi => {
      fi.ds.filter(_.isInstanceOf[JsonCollection.Dynamic]).foreach(di => {
        di.clean()
      })
    })
  }

  def invokeMapIter(functions: Map[String, FunctionInstruction],
                    context: ScriptContext,
                    m: parser.MapIterInstruction, funcName: Option[String]): Seq[JsonCollection.Val] = {
    val res: Seq[JsonCollection.Val] = m.arr.value.toList.map(i => {
      val fun = m.fun
      val fs: mutable.Map[String, FunctionInstruction] = mutable.Map[String, FunctionInstruction]()
      fs.addAll(functions)
      fs.put(fun.funcName + fun.variableNames.size, fun)
      clearContextBeforeInvoke(fun.instructions)
      val invoke = FunctionInvokeInstruction(fun.funcName, Seq(i))
      runInstructions(fs.toMap, context, Seq(invoke), funcName).lastOption
    }).filter(_.isDefined).map(_.get)
    Seq(JsonCollection.Arr(res: _*))
  }

  def runInstructions(functions: Map[String, FunctionInstruction],
                      context: ScriptContext,
                      instructions: Seq[Instruction2], funName: Option[String] = None): Seq[JsonCollection.Val] = {
    context.variables.put("INVOKE_PATH", new JsonCollection.Str(funName.orNull))
    try {
      instructions.foreach(po => {
        evalDs(functions, context, po.ds, funName)
      })

      instructions
        .filter(!_.isInstanceOf[ScriptContextInstruction2]).flatMap {
          case r: ForInstruction => {
            iterCollection(functions, context, r)
          }
          case f: FunctionInvokeInstruction =>
            invokeFunction(functions, context, f, funName)
          case r: ReturnInstruction => {
            Seq(r.value.copy)
          }
          case m: MapIterInstruction =>
            invokeMapIter(functions, context, m, funName)
          case i => {
            val json = i.execute(context).json
            parser.parseJson(json) match {
              case Success(j) => Seq(j)
              case Failure(f) => Seq(JsonCollection.Str(json))
            }
          }
        }
    } finally {
      context.variables.remove("INVOKE_PATH")
    }

  }

  def evalDs(functions: Map[String, FunctionInstruction],
             context: ScriptContext,
             vars: Seq[JsonCollection.Dynamic], funName: Option[String] = None) = {
    vars.foreach {
      case vr: JsonCollection.Var =>
        mapRealValue(context.variables, vr, funName)
      case v: JsonCollection.ArithTree =>
        evalArith(functions, context, v, funName)
      case f: JsonCollection.Fun =>
        val res = invokeFunction(functions, context,
          FunctionInvokeInstruction(f.value._1, f.value._2), funName).last
        f.realValue = Some(res)
    }
  }


  def evalArith(functions: Map[String, FunctionInstruction],
                context: ScriptContext,
                v: JsonCollection.ArithTree,
                funName: Option[String] = None): Unit = {
    v.op match {
      case Some("+") =>
        val v1 = evalBasicValue(functions, context, v.a, funName)
        v.b match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.plus(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("-") =>
        val v1 = evalBasicValue(functions, context, v.a, funName)
        v.b match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.minus(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("*") =>
        val v1 = evalBasicValue(functions, context, v.a, funName)
        v.b match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.multiply(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("/") =>
        val v1 = evalBasicValue(functions, context, v.a, funName)
        v.b match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.div(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case None if v.a.isInstanceOf[JsonCollection.ArithTree] => {
        evalArith(functions, context, v.a.asInstanceOf[JsonCollection.ArithTree], funName)
        v.realValue = v.a.asInstanceOf[JsonCollection.ArithTree].realValue
      }
      case i if v.a.isInstanceOf[JsonCollection.Arith] => {
        v.realValue = Some(v.a.asInstanceOf[JsonCollection.Arith])
      }
    }
  }


  def evalBasicValue(functions: Map[String, FunctionInstruction],
                     context: ScriptContext,
                     v: JsonCollection.Val,
                     funName: Option[String]): JsonCollection.Arith =
    v match {
      case i: JsonCollection.ArithTree => {
        i.op match {
          case None => evalBasicValue(functions, context, i.a, funName)
          case t => {
            evalArith(functions, context, i, funName)
            i.realValue.get.asInstanceOf[JsonCollection.Arith]
          }
        }
      }
      case i: JsonCollection.Num => i
      case i: JsonCollection.Str => i
      case i: JsonCollection.Var => {
        mapRealValue(context.variables, i, funName)
        evalBasicValue(functions, context, i.realValue.get, funName)
      }
      case f: JsonCollection.Fun => {
        val res = invokeFunction(functions, context,
          FunctionInvokeInstruction(f.value._1, f.value._2), funName).lastOption
        f.realValue = res
        evalBasicValue(functions, context, res.getOrElse(JsonCollection.Null), funName)
      }
      case _ => throw new RuntimeException("only support num and str arith expression")
    }
}

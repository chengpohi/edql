package com.github.chengpohi.script

import com.github.chengpohi.context.{AuthInfo, HostInfo}
import com.github.chengpohi.parser.EDQLParser
import com.github.chengpohi.parser.collection.JsonCollection
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
  val eqlParser: EDQLParser
  val httpClient: HttpClient = HttpClient.newHttpClient()
  val libs: Seq[URL]

  import eqlParser._

  def invokeInstruction(invokeIns: Seq[eqlParser.Instruction2],
                        scriptContextIns: Seq[eqlParser.Instruction2],
                        runContext: EDQLRunContext): EDQLRunResult = {

    val endpointBind = scriptContextIns.find(_.isInstanceOf[EndpointBindInstruction])
      .map(i => i.asInstanceOf[eqlParser.EndpointBindInstruction])
    if (endpointBind.isEmpty && runContext.hostInfo == null) {
      return EDQLRunResult(Failure(new RuntimeException("should configure host")))
    }

    val (functions, context) = this.buildContext(scriptContextIns, endpointBind.get.endpoint, endpointBind.get.kibanaProxy, runContext)

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

  private def buildContext(cIns: Seq[eqlParser.Instruction2],
                           endPoint: String,
                           kibanaProxy: Boolean,
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
    val hostInfo = buildHostInfo(runContext, endPoint, kibanaProxy, invokeIns)
    val context = ScriptContext(hostInfo, globalVars)

    evalFunParams(globalFunctions, context, vars)
    (globalFunctions, context)
  }

  private def buildHostInfo(runContext: EDQLRunContext, endPoint: String, kibanaProxy: Boolean, invokeIns: Seq[Instruction2]): HostInfo = {
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
    val authInfo = AuthInfo(authorization, username, password, apikeyId, apikeySecret, apiSessionToken, awsRegion)
    HostInfo(endPoint, URI.create(endPoint), timeout, kibanaProxy, Some(authInfo))
  }

  private def parseImports(cIns: Seq[eqlParser.Instruction2], runDir: String): Seq[eqlParser.Instruction2] = {
    val imports =
      cIns.filter(_.isInstanceOf[ImportInstruction])
        .map(i => i.asInstanceOf[ImportInstruction])

    if (imports.isEmpty) {
      return Seq()
    }

    val importStr = imports.map(i => {
      handleImport(runDir, httpClient, i)
    }).mkString("").trim

    val importIns = eqlParser.generateInstructions(importStr) match {
      case Success(ins) => ins
      case Failure(f) => throw new RuntimeException("import parse failed" + f.getMessage, f)
    }
    importIns ++ parseImports(importIns, runDir).filter(i => i.isInstanceOf[FunctionInstruction] || i.isInstanceOf[VariableInstruction])
  }

  private def handleImport(runDir: String, client: HttpClient, i: eqlParser.ImportInstruction): String = {
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

  private def evalFunParams(globalFunctions: Map[String, eqlParser.FunctionInstruction],
                            context: ScriptContext,
                            parms: Map[String, JsonCollection.Val],
                            funName: Option[String] = None
                           ) = {
    parms.filter(_._2.isInstanceOf[JsonCollection.Fun])
      .foreach(i => {
        val fParam = i._2.asInstanceOf[JsonCollection.Fun]
        val value = invokeFunction(globalFunctions, context, FunctionInvokeInstruction(fParam.value._1, fParam.value._2), funName).last
        fParam.realValue = Some(value)
        context.variables.put(i._1, value)
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


  def iterCollection(functions: Map[String, eqlParser.FunctionInstruction],
                     context: ScriptContext,
                     r: eqlParser.ForInstruction): Seq[JsonCollection.Val] = {
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


  def invokeFunction(functions: Map[String, eqlParser.FunctionInstruction],
                     context: ScriptContext,
                     invoke: eqlParser.FunctionInvokeInstruction, parentFunName: Option[String] = None): Seq[JsonCollection.Val] = {
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
    response
  }

  private def clearContextBeforeInvoke(instructions: Seq[eqlParser.Instruction2]) = {
    instructions.foreach(fi => {
      fi.ds.filter(_.isInstanceOf[JsonCollection.Var]).foreach(di => {
        di.asInstanceOf[JsonCollection.Var].realValue = None
      })
    })
  }

  def runInstructions(functions: Map[String, eqlParser.FunctionInstruction],
                      context: ScriptContext,
                      instructions: Seq[eqlParser.Instruction2], funName: Option[String] = None): Seq[JsonCollection.Val] = {
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
          case i => {
            val json = i.execute(context).json
            parseJson(json) match {
              case Success(j) => Seq(j)
              case Failure(f) => Seq(JsonCollection.Str(json))
            }
          }
        }
    } finally {
      context.variables.remove("INVOKE_PATH")
    }

  }

  def evalDs(functions: Map[String, eqlParser.FunctionInstruction],
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


  def evalArith(functions: Map[String, eqlParser.FunctionInstruction],
                context: ScriptContext,
                v: JsonCollection.ArithTree,
                funName: Option[String] = None): Unit = {
    val value = v.value
    value._2 match {
      case Some("+") =>
        val v1 = evalBasicValue(functions, context, value._1, funName)
        value._3 match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.plus(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("-") =>
        val v1 = evalBasicValue(functions, context, value._1, funName)
        value._3 match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.minus(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("*") =>
        val v1 = evalBasicValue(functions, context, value._1, funName)
        value._3 match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.multiply(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case Some("/") =>
        val v1 = evalBasicValue(functions, context, value._1, funName)
        value._3 match {
          case Some(v3) => {
            val v2 = evalBasicValue(functions, context, v3, funName)
            v.realValue = Some(v1.div(v2))
          }
          case None => v.realValue = Some(v1)
        }
      case None if value._1.isInstanceOf[JsonCollection.ArithTree] => {
        evalArith(functions, context, value._1.asInstanceOf[JsonCollection.ArithTree], funName)
        v.realValue = value._1.asInstanceOf[JsonCollection.ArithTree].realValue
      }
      case i if value._1.isInstanceOf[JsonCollection.Arith] => {
        v.realValue = Some(v.value._1.asInstanceOf[JsonCollection.Arith])
      }
    }
  }


  def evalBasicValue(functions: Map[String, eqlParser.FunctionInstruction],
                     context: ScriptContext,
                     v: JsonCollection.Val,
                     funName: Option[String]): JsonCollection.Arith =
    v match {
      case i: JsonCollection.ArithTree => {
        i.value match {
          case (t, None, None) => evalBasicValue(functions, context, t, funName)
          case t => {
            evalArith(functions, context, i, funName)
            i.realValue.get
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
          FunctionInvokeInstruction(f.value._1, f.value._2), funName).last
        f.realValue = Some(res)
        evalBasicValue(functions, context, res, funName)
      }
      case _ => throw new RuntimeException("only support num and str arith expression")
    }
}

package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.parser.EQLParser
import com.github.chengpohi.parser.collection.JsonCollection
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Success, Try}

class EQLScriptRunner {
  val eqlParser: EQLParser = new EQLParser

  import eqlParser._

  def parse: String => PSI = (s: String) => instruction(s)

  def generateInstructions(source: String): Try[Seq[Instruction2]] = {
    (parse andThen gi).apply(source)
  }

  def readFile(file: File): Try[String] = Try {
    val script = Resource
      .fromAutoCloseable(IO {
        Source.fromFile(file)
      })
      .use(i => IO(i.getLines().mkString(System.lineSeparator())))
      .unsafeRunSync()
    script
  }

  def parseJson(text: String): Try[JsonCollection.Val] = {
    eqlParser.parseJson(text)
  }

  def extractVars(text: String): Map[String, Either[JsonCollection.Val, FunctionInvokeInstruction]] = {
    val instructions = this.generateInstructions(text)
    instructions.map(ins => {
      val cIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
      cIns.filter(_.isInstanceOf[VariableInstruction])
        .map(i => i.asInstanceOf[VariableInstruction])
        .map(i => i.variableName -> i.value)
        .toMap
    }) match {
      case Success(value) => value
      case Failure(_) => Map()
    }
  }

  def run(script: String, targetInstruction: Option[String] = None): Try[Seq[Seq[String]]] = {
    val instructions = this.generateInstructions(script)
    val selectedInstruction = targetInstruction.map(i => this.generateInstructions(i))

    if (selectedInstruction.exists(_.isFailure)) {
      return Failure(selectedInstruction.get.failed.get)
    }

    instructions.flatMap(ins => {
      val invokeIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
      val scriptContextIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
      selectedInstruction match {
        case Some(select) => {
          invokeInstruction(select.get, scriptContextIns)
        }
        case None =>
          invokeInstruction(invokeIns, scriptContextIns)
      }
    })
  }

  private def invokeInstruction(invokeIns: Seq[eqlParser.Instruction2],
                                scriptContextIns: Seq[eqlParser.Instruction2]
                               ): Try[Seq[Seq[String]]] = {

    val endpointBind = scriptContextIns.find(_.isInstanceOf[EndpointBindInstruction])
      .map(i => i.asInstanceOf[eqlParser.EndpointBindInstruction])
    if (endpointBind.isEmpty) {
      return Failure(new RuntimeException("Should bind host"))
    }

    val (functions, context) =
      this.buildContext(scriptContextIns, endpointBind.get.endpoint)

    val invokeResult = invokeIns.map {
      case invokeFunction: FunctionInvokeInstruction =>
        functionInvoke(functions, context, invokeFunction)
      case i =>
        Seq(i.execute(context).json)
    }

    Success(invokeResult)
  }

  def buildContext(cIns: Seq[eqlParser.Instruction2], endPoint: String) = {
    val authorization =
      cIns.find(_.isInstanceOf[AuthorizationBindInstruction])
        .map(i => i.asInstanceOf[AuthorizationBindInstruction])
        .map(i => i.auth)

    val timeout =
      cIns.find(_.isInstanceOf[TimeoutInstruction])
        .map(i => i.asInstanceOf[TimeoutInstruction])
        .map(i => i.timeout)

    val vars =
      cIns.filter(_.isInstanceOf[VariableInstruction])
        .map(i => i.asInstanceOf[VariableInstruction])
        .map(i => i.variableName -> i.value).toMap

    val globalFunctions =
      cIns.filter(_.isInstanceOf[FunctionInstruction])
        .map(i => i.asInstanceOf[FunctionInstruction])
        .map(i => i.funcName + i.variableNames.size -> i).toMap

    val globalVars = vars.filter(_._2.isLeft).map(i => i._1 -> i._2.left.get)

    val context = ScriptEQLContext(
      endPoint,
      authorization,
      timeout,
      globalVars)

    evaluateFunctionVars(globalFunctions, context, vars)

    (globalFunctions, context)
  }

  private def evaluateFunctionVars(globalFunctions: Map[String, eqlParser.FunctionInstruction],
                                   context: ScriptEQLContext,
                                   vars: Map[String, eqlParser.ContextVal]
                                  ) = {
    val evaluateVars = vars.filter(_._2.isRight).map(i => i._1 -> i._2.right.get)
    evaluateVars.foreach(fVar => {
      val value = functionInvoke(globalFunctions, context, fVar._2).last
      val fVal = parseJson(value)
      if (fVal.isFailure) {
        throw new RuntimeException(fVal.failed.get)
      }
      context.variables.put(fVar._1, fVal.get)
    })
    val valVars = vars.filter(_._2.isLeft).map(i => i._1 -> i._2.left.get)
    context.variables.addAll(valVars)
  }

  def functionInvoke(functions: Map[String, eqlParser.FunctionInstruction],
                     context: ScriptEQLContext,
                     invoke: eqlParser.FunctionInvokeInstruction): Seq[String] = {
    val cachedVariables = context.variables

    val values = invoke.vals
    val foundFunction = functions.get(invoke.funcName + values.size)
    if (foundFunction.isEmpty) {
      throw new RuntimeException("Could found method: " + invoke.funcName + " with parameters" + values.toString())
    }

    val func = foundFunction.get
    val funcMethodVars = func.variableNames.zip(values).toMap

    evaluateFunctionVars(functions, context, funcMethodVars)

    context.variables = mutable.Map[String, JsonCollection.Val](cachedVariables.toSeq: _*)

    val funcBodyVars = func.instructions.filter(_.isInstanceOf[VariableInstruction])
      .map(i => i.asInstanceOf[VariableInstruction])
      .map(i => i.variableName -> i.value).toMap

    evaluateFunctionVars(functions, context, funcBodyVars)

    val response = func.instructions
      .filter(!_.isInstanceOf[ScriptContextInstruction2]).flatMap {
      case f: FunctionInvokeInstruction =>
        functionInvoke(functions, context, f)
      case r: ReturnInstruction => {
        r.value match {
          case Left(jv) => {
            mapRealValue(context.variables, jv)
            Seq(jv.toJson)
          }
          case Right(f) => functionInvoke(functions, context, f)
        }
      }
      case i => {
        Seq(i.execute(context).json)
      }
    }
    context.variables = cachedVariables
    response
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.EQLParser
import com.github.chengpohi.parser.collection.JsonCollection
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.net.URL
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

  def extractVars(text: String): Map[String, JsonCollection.Val] = {
    val instructions = this.generateInstructions(text)
    instructions.map(ins => {
      val cIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
      cIns.filter(_.isInstanceOf[VariableInstruction])
        .map(i => i.asInstanceOf[VariableInstruction])
        .map(i => i.variableName -> i.value)
        .toMap
    }) match {
      case Success(value) => value
      case Failure(_) => Map[String, JsonCollection.Val]()
    }
  }

  def run(script: String): Try[Seq[Seq[String]]] = {
    val instructions = this.generateInstructions(script)
    instructions.map(ins => {
      val rIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
      val cIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])

      cIns.find(_.isInstanceOf[EndpointBindInstruction]) match {
        case Some(h) =>
          val hostInstruction2 = h.asInstanceOf[EndpointBindInstruction]
          val aInstruction2 = cIns.find(_.isInstanceOf[AuthorizationBindInstruction])
            .map(i => i.asInstanceOf[AuthorizationBindInstruction]).map(i => i.auth)
          val timeout = cIns.find(_.isInstanceOf[TimeoutInstruction])
            .map(i => i.asInstanceOf[TimeoutInstruction]).map(i => i.timeout)
          val vars = cIns.filter(_.isInstanceOf[VariableInstruction])
            .map(i => i.asInstanceOf[VariableInstruction])
            .map(i => i.variableName -> i.value).toMap

          val functions = cIns.filter(_.isInstanceOf[FunctionInstruction])
            .map(i => i.asInstanceOf[FunctionInstruction])
            .map(i => i.funcName + i.variableNames.size -> i).toMap

          val context = ScriptEQLContext(hostInstruction2.endpoint, aInstruction2, timeout, vars)
          rIns.map {
            case f: FunctionInvokeInstruction =>
              functionInvoke(functions, context, f)
            case i => {
              Seq(i.execute(context).json)
            }
          }
        case None =>
          return Failure(new RuntimeException("Please set host bind"))
      }
    })
  }

  private def functionInvoke(functions: Map[String, eqlParser.FunctionInstruction],
                             context: ScriptEQLContext,
                             invoke: eqlParser.FunctionInvokeInstruction): Seq[String] = {
    val values = invoke.vals
    val maybeInstruction = functions.get(invoke.funcName + values.size)
    if (maybeInstruction.isEmpty) {
      throw new RuntimeException("Could found method: " + invoke.funcName + " with parameters" + values.toString())
    }

    val func = maybeInstruction.get
    val vars = func.variableNames.zip(values).toMap
    val variables = context.variables
    val funcVars = func.instructions.filter(_.isInstanceOf[VariableInstruction])
      .map(i => i.asInstanceOf[VariableInstruction])
      .map(i => i.variableName -> i.value).toMap

    context.variables = (variables.toSeq ++ vars.toSeq ++ funcVars).groupBy(_._1).view.mapValues(_.map(_._2).head).toMap
    val response = func.instructions.filter(!_.isInstanceOf[ScriptContextInstruction2]).flatMap {
      case f: FunctionInvokeInstruction =>
        functionInvoke(functions, context, f)
      case i => {
        Seq(i.execute(context).json)
      }
    }

    context.variables = variables
    response
  }

  private def scriptContextInstruction(i: eqlParser.Instruction2) = {
    i.isInstanceOf[ScriptContextInstruction2]
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

class ScriptEQLContext(host: String, port: Int, auth: Option[String], timeout: Option[Int]) extends EQLConfig with EQLContext {
  override implicit lazy val eqlClient: EQLClient =
    buildRestClient(host, port, auth, timeout)
}

object ScriptEQLContext {
  def apply(endpoint: String, auth: Option[String] = None, timeout: Option[Int], vars: Map[String, JsonCollection.Val]): ScriptEQLContext = {
    val url = new URL(endpoint)
    val context = new ScriptEQLContext(url.getHost, url.getPort, auth, timeout)
    context.variables = vars
    context
  }
}

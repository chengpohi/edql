package com.github.chengpohi.script

import cats.effect.{IO, Resource}
import com.github.chengpohi.parser.EQLParser
import com.github.chengpohi.parser.collection.JsonCollection
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

class EQLScriptRunner extends InstructionInvoker {
  override val eqlParser: EQLParser = new EQLParser

  import eqlParser._

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
    val instructions = eqlParser.generateInstructions(text)
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

  def run(script: String,
          targetInstruction: Option[String] = None,
          runDir: Option[String] = None): Try[Seq[Seq[String]]] = {
    val instructions = eqlParser.generateInstructions(script)
    val selectedInstruction = targetInstruction.map(i => eqlParser.generateInstructions(i))

    if (selectedInstruction.exists(_.isFailure)) {
      return Failure(selectedInstruction.get.failed.get)
    }

    instructions.flatMap(ins => {
      val invokeIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
      val scriptContextIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
      selectedInstruction match {
        case Some(select) => {
          this.invokeInstruction(select.get, scriptContextIns, runDir.getOrElse(""))
        }
        case None =>
          this.invokeInstruction(invokeIns, scriptContextIns, runDir.getOrElse(""))
      }
    })
  }


  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

package com.github.chengpohi.script

import com.github.chengpohi.parser.EQLParser
import com.github.chengpohi.parser.collection.JsonCollection
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors
import scala.util.{Failure, Success, Try}

class EQLScriptRunner extends InstructionInvoker {
  override val eqlParser: EQLParser = new EQLParser

  import eqlParser._

  def readFile(file: File): Try[String] = Try {
    val script =
      Files.readAllLines(file.toPath).stream().collect(Collectors.joining(System.lineSeparator()))
    script
  }

  def parseJson(text: String): Try[JsonCollection.Val] = {
    eqlParser.parseJson(text)
  }

  def extractVars(text: String): Map[String, JsonCollection.Val] = {
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

  def run(script: String, eqlRunContext: EQLRunContext = EQLRunContext()): EQLRunResult = {
    val instructions = eqlParser.generateInstructions(script)
    val selectedInstruction = eqlRunContext.targetInstruction.map(i => eqlParser.generateInstructions(i))

    if (selectedInstruction.exists(_.isFailure)) {
      return EQLRunResult(Failure(selectedInstruction.get.failed.get))
    }

    instructions match {
      case Success(ins) => {
        val invokeIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
        val scriptContextIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
        selectedInstruction match {
          case Some(select) => {
            this.invokeInstruction(select.get, scriptContextIns, eqlRunContext.runDir)
          }
          case None =>
            this.invokeInstruction(invokeIns, scriptContextIns, eqlRunContext.runDir)
        }
      }
      case Failure(f) => {
        EQLRunResult(Failure(f))
      }
    }
  }

  def getScriptFilePathFromEnv: Option[String] = {
    val config: Config = ConfigFactory.load()
    config.hasPath("eql.file") match {
      case true => Some(config.getString("eql.file"))
      case false => None
    }
  }
}

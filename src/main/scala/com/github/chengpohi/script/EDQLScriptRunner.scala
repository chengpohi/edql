package com.github.chengpohi.script

import com.github.chengpohi.edql.parser.json.JsonCollection
import com.github.chengpohi.edql.parser.{EDQLParserDefinition, EDQLParserFactory}
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.typesafe.config.{Config, ConfigFactory}

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.stream.Collectors
import scala.util.{Failure, Success, Try}

class EDQLScriptRunner(ls: Seq[URL], psiFileFactoryImpl: PsiFileFactoryImpl) extends InstructionInvoker {
  override val factory: EDQLParserFactory = new EDQLParserFactory("edql", new EDQLParserDefinition, psiFileFactoryImpl)

  import parser._

  def readFile(file: File): Try[String] = Try {
    val script =
      Files.readAllLines(file.toPath).stream().collect(Collectors.joining(System.lineSeparator()))
    script
  }

  def parseJson(text: String): Try[JsonCollection.Val] = {
    parser.parseJson(text)
  }

  def extractVars(text: String): Map[String, JsonCollection.Val] = {
    val instructions = parse(text)
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

  def run(script: String, runContext: EDQLRunContext = EDQLRunContext()): EDQLRunResult = {
    val instructions = parse(script)
    val selectedInstruction = Option.apply(runContext.targetInstruction).map(i => parse(i))

    if (selectedInstruction.exists(_.isFailure)) {
      return EDQLRunResult(Failure(selectedInstruction.get.failed.get))
    }

    instructions match {
      case Success(ins) => {
        val invokeIns = ins.filter(!_.isInstanceOf[ScriptContextInstruction2])
        val scriptContextIns = ins.filter(_.isInstanceOf[ScriptContextInstruction2])
        selectedInstruction match {
          case Some(select) => {
            this.invokeInstruction(select.get, scriptContextIns, runContext)
          }
          case None =>
            this.invokeInstruction(invokeIns, scriptContextIns, runContext)
        }
      }
      case Failure(f) => {
        EDQLRunResult(Failure(f))
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

  override val libs: Seq[URL] = ls

  def close(): Unit = {
    for (c <- ScriptContext.cache) {
      c._2._2.restClient.close()
    }
  }

}

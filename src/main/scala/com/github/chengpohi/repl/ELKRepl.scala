package com.github.chengpohi.repl

import java.io.File

import com.github.chengpohi.api.serializer.JSONOps
import com.github.chengpohi.connector.ELKDSLConfig
import com.github.chengpohi.registry.ELKDSLContext
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import jline.internal.Configuration
import scalaz._
import Scalaz._

import scala.io.Source

/**
  * elasticdsl
  * Created by chengpohi on 1/27/16.
  */
object ELKRepl extends ELKDSLConfig with ELKDSLContext with JSONOps {
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticdsl"
  val terms = new StringsCompleter(
    Source.fromURL(getClass.getResource("/completions.txt")).getLines().toSet,
    Source.fromURL(getClass.getResource("/words.txt")).getLines().toSet)
  val elkInterpreter = new ELKInterpreter()

  import dsl._

  def main(args: Array[String]): Unit = {
    val reader: ConsoleReader = buildReader

    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      line.trim match {
        case "exit" => System.exit(0)
        case l      => run(interpret(l)).println
      }
    }
  }

  def interpret(line: String): Reader[ELKInterpreter, String] = {
    Reader((elkRunEngine: ELKInterpreter) => elkRunEngine.run(line).beautify)
  }

  def run[T](reader: Reader[ELKInterpreter, T]) = {
    reader(elkInterpreter)
  }

  def addShutdownHook(reader: ConsoleReader): Unit = {
    DSL {
      create index ELASTIC_SHELL_INDEX_NAME
    }.await

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        reader.getHistory.asInstanceOf[FileHistory].flush()
        DSL {
          delete index ELASTIC_SHELL_INDEX_NAME
        }.await
      }
    })
  }
  def buildReader: ConsoleReader = {
    val reader = new ConsoleReader()
    reader.setPrompt("elasticdsl>")
    reader.addCompleter(terms)
    reader.setCompletionHandler(new ELKCompletionHandler)
    reader.setHistory(
      new FileHistory(
        new File(Configuration.getUserHome, ".elasticdsl.history")))
    addShutdownHook(reader)
    reader
  }
}

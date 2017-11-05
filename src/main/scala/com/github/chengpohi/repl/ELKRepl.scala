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
  val eLKCompletionHandler = new ELKCompletionHandler
  val elkRunEngine = new ELKInterpreter()

  import dsl._

  def main(args: Array[String]): Unit = {
    val reader = new ConsoleReader()
    reader.setPrompt("elasticdsl>")
    reader.addCompleter(terms)
    reader.setCompletionHandler(eLKCompletionHandler)
    reader.setHistory(
      new FileHistory(
        new File(Configuration.getUserHome, ".elasticdsl.history")))
    addShutdownHook(reader)

    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      line.trim.isEmpty match {
        case true =>
        case false =>
          try {
            elkRunEngine.run(line).beautify.println
          } catch {
            case e: Exception =>
              println(e.getMessage)
          }
      }
    }
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
}

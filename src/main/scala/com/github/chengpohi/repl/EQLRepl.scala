package com.github.chengpohi.repl

import java.io.File

import com.github.chengpohi.api.serializer.JSONOps
import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.registry.EQLContext
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import jline.internal.Configuration
import org.apache.lucene.util.IOUtils
import scalaz._

import scala.io.Source


//noinspection ScalaStyle
object EQLRepl extends EQLConfig with EQLContext with JSONOps {
  val ELASTIC_SHELL_INDEX_NAME: String = ".eql"
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_RESET = "\u001B[0m"
  val terms = new StringsCompleter(
    Source.fromURL(getClass.getResource("/completions.txt")).getLines().toSet,
    Source.fromURL(getClass.getResource("/words.txt")).getLines().toSet)
  val elkInterpreter = new EQLInterpreter()

  import eql._

  def main(args: Array[String]): Unit = {
    println(ANSI_GREEN + "Welcome to EQL Repl :)" + ANSI_RESET)
    val reader: ConsoleReader = buildReader

    while (true) {
      val line = reader.readLine()
      line.trim match {
        case "exit" => System.exit(0)
        case l => println(run(interpret(l)))
      }
    }
  }

  def interpret(line: String): Reader[EQLInterpreter, String] = {
    Reader((elkRunEngine: EQLInterpreter) => elkRunEngine.run(line).beautify)
  }

  def run[T](reader: Reader[EQLInterpreter, T]): Id.Id[T] = {
    reader(elkInterpreter)
  }

  def addShutdownHook(reader: ConsoleReader): Unit = {
    EQL {
      create index ELASTIC_SHELL_INDEX_NAME not exist
    }.await

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        println(ANSI_GREEN + "exiting..." + ANSI_RESET)
        IOUtils.close(client)
        reader.getHistory.asInstanceOf[FileHistory].flush()
      }
    })
  }
  def buildReader: ConsoleReader = {
    val reader = new ConsoleReader()
    reader.setPrompt(ANSI_GREEN +  "eql>" + ANSI_RESET)
    reader.addCompleter(terms)
    reader.setCompletionHandler(new EQLCompletionHandler)
    reader.setHistory(
      new FileHistory(new File(Configuration.getUserHome, ".eql.history")))
    addShutdownHook(reader)
    reader
  }
}

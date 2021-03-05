package com.github.chengpohi.repl

import cats.Id
import cats.data.Reader
import cats.effect.{IO, Resource}
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.serializer.JSONOps
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import jline.internal.Configuration
import org.apache.lucene.util.IOUtils

import java.io.File
import scala.io.Source

class EQLReplRunner extends EQLConfig
  with EQLContext
  with JSONOps {
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_RESET = "\u001B[0m"

  val eqlInterpreter = new EQLReplInterpreter(this)

  def interpret(line: String): Reader[EQLReplInterpreter, String] = {
    Reader((interpreter: EQLReplInterpreter) => interpreter.run(line).beautify)
  }

  def run[T](reader: Reader[EQLReplInterpreter, T]): Id[T] = {
    reader(eqlInterpreter)
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
    reader.setPrompt(ANSI_GREEN + "eql>" + ANSI_RESET)
    reader.addCompleter(generateCompleter())
    reader.setCompletionHandler(new EQLCompletionHandler)
    reader.setHistory(
      new FileHistory(new File(Configuration.getUserHome, ".eql.history")))
    addShutdownHook(reader)
    reader
  }


  def generateCompleter(): StringsCompleter = {
    val completions = Resource
      .fromAutoCloseable(IO {
        Source.fromURL(getClass.getResource("/completions.txt"))
      })
      .use(i => IO(i.getLines().toSet))
      .unsafeRunSync()

    val words: Set[String] = Resource
      .fromAutoCloseable(IO {
        Source.fromURL(getClass.getResource("/words.txt"))
      })
      .use(i => IO(i.getLines().toSet))
      .unsafeRunSync()
    new StringsCompleter(completions, words)
  }


  def runRepl() = {
    println(ANSI_GREEN + "Welcome to EQL Repl :)" + ANSI_RESET)
    val reader: ConsoleReader = buildReader
    while (true) {
      val line = reader.readLine()
      Option(line).map(_.trim) match {
        case Some("exit") =>
          System.exit(0)
        case Some(l) =>
          val result = run(interpret(l))
          println(result)
        case None =>
          System.exit(0)
      }
    }
  }
}

object EQLReplRunner {
  def apply(): EQLReplRunner = new EQLReplRunner()
}

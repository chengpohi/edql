package com.github.chengpohi.repl

import cats.Id
import cats.data.Reader
import cats.effect._
import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.dsl.serializer.JSONOps
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import jline.internal.Configuration
import joptsimple.OptionParser
import org.apache.commons.lang3.StringUtils
import org.apache.lucene.util.IOUtils

import java.io.File
import scala.collection.JavaConverters._
import scala.io.Source

object EQLRepl extends EQLConfig with EQLContext with JSONOps {
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_RESET = "\u001B[0m"

  val elkInterpreter = new EQLInterpreter()

  def interpret(line: String): Reader[EQLInterpreter, String] = {
    Reader((elkRunEngine: EQLInterpreter) => elkRunEngine.run(line).beautify)
  }

  def run[T](reader: Reader[EQLInterpreter, T]): Id[T] = {
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


  def repl() = {
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


  def main(args: Array[String]): Unit = {
    val parser = new OptionParser();
    val scriptFileOpt = parser.acceptsAll(List("f", "file").asJava, "script file")
      .withOptionalArg()
    val optionSets = parser.parse(args: _*)
    val scriptFile = optionSets.valueOf(scriptFileOpt)
    if (StringUtils.isNotBlank(scriptFile)) {
      val script = Resource
        .fromAutoCloseable(IO {
          Source.fromURL(scriptFile)
        })
        .use(i => IO(i.getLines().mkString(System.lineSeparator())))
        .unsafeRunSync()
      val result = run(interpret(script))
      println(result)
    }

    repl()
  }
}

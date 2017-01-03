package com.github.chengpohi.repl

import java.io.File

import com.github.chengpohi.ELKInterpreter
import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.registry.ELKCommandRegistry
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import jline.internal.Configuration

import scala.io.Source
import scala.util.Try

/**
  * elasticdsl
  * Created by chengpohi on 1/27/16.
  */
object ELKRepl {
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticdsl"
  private val generator = new ResponseGenerator
  val terms = new StringsCompleter(Source.fromURL(getClass.getResource("/completions.txt")).getLines().toSet,
    Source.fromURL(getClass.getResource("/words.txt")).getLines().toSet)
  val eLKCompletionHandler = new ELKCompletionHandler
  val elkRunEngine = new ELKInterpreter(ELKCommandRegistry)

  def main(args: Array[String]): Unit = {
    val reader = new ConsoleReader()
    reader.setPrompt("elasticdsl>")
    reader.addCompleter(terms)
    reader.setCompletionHandler(eLKCompletionHandler)
    reader.setHistory(new FileHistory(new File(Configuration.getUserHome, ".elasticdsl.history")))
    addShutdownHook(reader)

    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      line.trim.isEmpty match {
        case true =>
        case false =>
          try {
            val res = generator.beautyJSON(elkRunEngine.run(line))
            println(res)
          } catch {
            case e: Exception =>
              println(e.getMessage)
          }
      }
    }
  }

  def addShutdownHook(reader: ConsoleReader): Unit = {
    try {
      elkRunEngine.run(s"""create index "$ELASTIC_SHELL_INDEX_NAME"""")
    } catch {
      case e: Exception => println(e)
    }
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        reader.getHistory.asInstanceOf[FileHistory].flush()
        elkRunEngine.run(s"""delete "$ELASTIC_SHELL_INDEX_NAME"""")
      }
    })
  }
}

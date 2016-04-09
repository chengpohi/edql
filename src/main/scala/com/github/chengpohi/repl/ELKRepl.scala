package com.github.chengpohi.repl

import java.io.PrintWriter

import com.github.chengpohi.ELKRunEngine
import com.github.chengpohi.registry.ELKCommandRegistry
import jline.console.ConsoleReader

import scala.io.Source

/**
  * elasticshell
  * Created by chengpohi on 1/27/16.
  */
object ELKRepl {
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticshell"
  val terms = new StringsCompleter(Source.fromURL(getClass.getResource("/completions.txt")).getLines().toSet,
    Source.fromURL(getClass.getResource("/words.txt")).getLines().toSet)
  val eLKCompletionHandler = new ELKCompletionHandler
  val elkRunEngine = new ELKRunEngine(ELKCommandRegistry)


  def main(args: Array[String]): Unit = {
    beforeStart()

    val reader = new ConsoleReader()
    reader.setPrompt("elasticshell>")
    reader.addCompleter(terms)
    reader.setCompletionHandler(eLKCompletionHandler)

    val out = new PrintWriter(reader.getOutput)
    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      elkRunEngine.run(line)
    }
  }

  def beforeStart() = {
    elkRunEngine.run(s"""create index "$ELASTIC_SHELL_INDEX_NAME"""")
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        elkRunEngine.run(s"""delete "$ELASTIC_SHELL_INDEX_NAME"""")
      }
    })
  }
}

package com.github.chengpohi.repl

import java.io.PrintWriter

import com.github.chengpohi.ELKRunEngine
import jline.console.ConsoleReader

import scala.io.Source

/**
 * elasticshell
 * Created by chengpohi on 1/27/16.
 */
object ELKRepl {
  val terms = new StringsCompleter(Source.fromURL(getClass.getResource("/completions.txt")).getLines().toSet)
  val eLKCompletionHandler = new ELKCompletionHandler

  def main(args: Array[String]): Unit = {
    val reader = new ConsoleReader()
    reader.setPrompt("elasticshell>")
    reader.addCompleter(terms)
    reader.setCompletionHandler(eLKCompletionHandler)
    val out = new PrintWriter(reader.getOutput)
    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      ELKRunEngine.run(line)
    }
  }
}

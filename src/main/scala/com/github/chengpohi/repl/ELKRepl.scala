package com.github.chengpohi.repl

import java.io.PrintWriter

import com.github.chengpohi.ELKRunEngine
import jline.console.ConsoleReader

/**
 * elasticshell
 * Created by chengpohi on 1/27/16.
 */
object ELKRepl {
  def main(args: Array[String]): Unit = {
    val reader = new ConsoleReader()
    reader.setPrompt("elasticshell>")
    val out = new PrintWriter(reader.getOutput())
    while (true) {
      val line = reader.readLine()
      if (line == "exit") System.exit(0)
      ELKRunEngine.run(line)
    }
  }
}

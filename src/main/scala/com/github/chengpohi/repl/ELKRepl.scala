package com.github.chengpohi.repl

import com.github.chengpohi.ELKRunEngine

import scala.io.StdIn

/**
 * elasticshell
 * Created by chengpohi on 1/27/16.
 */
object ELKRepl {
  def main(args: Array[String]): Unit = {
    while (true) {
      print("input>")
      val str = StdIn.readLine()
      if (str == "exit") System.exit(0)
      ELKRunEngine.run(str)
    }
  }
}

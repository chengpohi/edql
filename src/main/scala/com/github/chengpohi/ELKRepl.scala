package com.github.chengpohi

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
      ELKRunEngine.run(str)
    }
  }
}

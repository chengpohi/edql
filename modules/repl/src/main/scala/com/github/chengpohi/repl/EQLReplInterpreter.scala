package com.github.chengpohi.repl

import com.github.chengpohi.context.{EQLConfig, EQLContext}
import com.github.chengpohi.parser.EQLParser

import scala.io.Source

class EQLReplInterpreter(eql: EQLContext) {
  val eqlParser: EQLParser = new EQLParser

  import eqlParser._

  def run(source: String): String = {
    ""
  }
}

object EQLReplInterpreter extends EQLContext with EQLConfig {
  private val runEngine: EQLReplInterpreter = new EQLReplInterpreter(this)

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage eql file.eql")
      System.exit(0)
    }
    val parseFile: String = Source
      .fromFile(args(0))
      .getLines()
      .filter(!_.trim().startsWith("//"))
      .toList
      .mkString("")
    println(runEngine.run(parseFile))
  }
}

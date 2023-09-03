package com.github.chengpohi.repl

import com.github.chengpohi.context.{EDQLConfig, Context}
import com.github.chengpohi.parser.EDQLParser

import scala.io.Source
import scala.util.{Failure, Success}

class ReplInterpreter$(eql: Context) {
  val eqlParser: EDQLParser = new EDQLParser

  import eqlParser._

  def render(parsed: PSI): String = {
    val instructions = gi(parsed)

    instructions.map(_.map(j => j.execute(eql).json)) match {
      case Success(value) => value.mkString(System.lineSeparator())
      case Failure(exception) => exception.getMessage
    }
  }

  def parse: String => PSI = (s: String) => instruction(s)

  def run(source: String): String = {
    (parse andThen render).apply(source)
  }
}

object ReplInterpreter$ extends Context with EDQLConfig {
  private val runEngine: ReplInterpreter$ = new ReplInterpreter$(this)

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

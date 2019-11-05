package com.github.chengpohi.repl

import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.context.EQLContext
import com.github.chengpohi.parser.EQLParser

import scala.io.Source

class EQLInterpreter(implicit val elkParser: EQLParser) {

  import elkParser._

  def render(parsed: PSI): String = {
    val instructions = generateDefinitions(parsed)

    val res = for {
      instruction <- instructions
    } yield {
      try {
        instruction.f.apply(instruction.params).json
      } catch {
        case ex: Exception => s"unhandle error: ${ex.getMessage}"
      }
    }
    res.mkString("\n")
  }

  def parse: String => PSI = (s: String) => instructionParser.parse(s)

  def run(source: String): String = {
    (parse andThen render).apply(source)
  }

}

object EQLInterpreter extends EQLContext with EQLConfig {
  private val runEngine: EQLInterpreter = new EQLInterpreter()

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage elk file.elk")
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

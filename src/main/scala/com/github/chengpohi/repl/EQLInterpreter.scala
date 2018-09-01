package com.github.chengpohi.repl

import com.github.chengpohi.connector.EQLConfig
import com.github.chengpohi.parser.EQLParser
import com.github.chengpohi.registry.EQLContext

import scala.io.Source

class EQLInterpreter(implicit val elkParser: EQLParser) {

  import elkParser._

  def interceptDefinitions(
      instructions: Seq[elkParser.interceptFunction.Instruction]): String = {
    val res = for {
      instruction <- instructions
    } yield instruction.f.apply(instruction.params).json
    res.mkString("\n")
  }

  def run(source: String): String = {
    val parsed = instructionParser.parse(source)
    val instruments = generateDefinitions(parsed)
    interceptDefinitions(instruments)
  }

  def run(str: String, parameters: String*): String = {
    val s = String.format(str, parameters: _*)
    run(s)
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

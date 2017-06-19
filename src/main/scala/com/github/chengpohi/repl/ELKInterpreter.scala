package com.github.chengpohi.repl

import com.github.chengpohi.connector.ELKDSLConfig
import com.github.chengpohi.parser.ELKParser
import com.github.chengpohi.registry.ELKDSLContext

import scala.io.Source

/**
  * Created by chengpohi on 1/3/16.
  */
class ELKInterpreter(implicit val elkParser: ELKParser) {

  import elkParser._

  def interceptDefinitions(instructions: Seq[Instruction]): String = {
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

object ELKInterpreter extends ELKDSLContext with ELKDSLConfig{
  private val runEngine: ELKInterpreter = new ELKInterpreter()

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

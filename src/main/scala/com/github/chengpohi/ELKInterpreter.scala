package com.github.chengpohi

import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.ELKParser
import com.github.chengpohi.registry.ELKCommandRegistry

import scala.io.Source

/**
  * Created by chengpohi on 1/3/16.
  */
class ELKInterpreter(env: {val elkParser: ELKParser; val responseGenerator: ResponseGenerator}) {
  import env.elkParser._

  def interceptDefinitions(definitions: Seq[INSTRUMENT_TYPE]): String = {
    val res = for {
      definition <- definitions
    } yield definition._2.get.apply(definition._3).json
    res.mkString("\n")
  }

  def run(source: String): String = {
    val parsed = elkParser.parse(source)
    val instruments = generateAST(parsed)
    interceptDefinitions(instruments)
  }

  def run(str: String, parameters: String*): String = {
    val s = String.format(str, parameters: _*)
    run(s)
  }
}

object ELKInterpreter {
  private val runEngine: ELKInterpreter = new ELKInterpreter(ELKCommandRegistry)

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage elk file.elk")
      System.exit(0)
    }
    val parseFile: String = Source.fromFile(args(0)).getLines().filter(!_.trim().startsWith("//")).toList.mkString("")
    println(runEngine.run(parseFile))
  }
}


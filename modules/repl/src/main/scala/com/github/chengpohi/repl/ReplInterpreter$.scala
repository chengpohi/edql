package com.github.chengpohi.repl

import com.github.chengpohi.context.{Context, EDQLConfig}
import com.github.chengpohi.edql.parser.{EDQLParserDefinition, EDQLParserFactory, EDQLPsiInterceptor}

import scala.io.Source

class ReplInterpreter$(eql: Context) {
  private val factory: EDQLParserFactory = EDQLParserFactory.apply("edql", new EDQLParserDefinition)
  val parser = new EDQLPsiInterceptor(factory)

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
    //    println(runEngine.run(parseFile))
  }
}

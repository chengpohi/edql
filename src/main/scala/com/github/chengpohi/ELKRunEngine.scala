package com.github.chengpohi

import com.github.chengpohi.parser.ELK
import com.github.chengpohi.parser.ELKParser._
import org.elasticsearch.indices.IndexMissingException

import scala.io.Source

/**
 * scala-parser-combinator
 * Created by chengpohi on 1/3/16.
 */
class ELKRunEngine(functions: Map[String, (Seq[ELK.Instrument], Map[String, String])]) {
  def runInstruments(instruments: Seq[ELK.Instrument], variables: Map[String, String]): Unit = {
    instruments.foreach {
      case i: ELK.Instrument => i.value match {
        case (name, None, parameters) =>
          val (inss, vs) = functions.getOrElse(name, (Seq(), Map[String, String]()))
          val vss = vs.keys.toList.zip(parameters).toMap
          runInstruments(inss, vss)
        case (name, Some(ins), parameters) =>
          val ps: Seq[String] = parameters.map(s => {
            variables.getOrElse(s, s)
          })
          try {
            println(ins(ps))
          } catch {
            case e: IndexMissingException => println(e.getCause.getLocalizedMessage)
            case e: Exception => println(e.getCause.getLocalizedMessage)
          }
      }
    }
  }
}

object ELKRunEngine {
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage elk file.elk")
      System.exit(0)
    }
    val parseFile: String = Source.fromFile(args(0)).getLines().filter(!_.trim().startsWith("//")).toList.mkString("")
    run(parseFile)
  }

  def run(source: String): Unit = {
    val parsed = elkParser.parse(source)

    val (functions, instruments) = generateAST(parsed)
    new ELKRunEngine(functions).runInstruments(instruments, Map())
  }
}

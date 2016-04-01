package com.github.chengpohi

import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.ELK
import com.github.chengpohi.parser.ELKParser
import scala.io.Source

/**
 * Created by chengpohi on 1/3/16.
 */
class ELKRunEngine(functions: Map[String, (Seq[ELK.Instrument], Map[String, String])]) {
  val responseGenerator = new ResponseGenerator
  import responseGenerator._
  def runInstruments(instruments: Seq[ELK.Instrument], variables: Map[String, String]): Unit = {
    instruments.foreach {
      case i: ELK.Instrument => i.value match {
        case (name, Some(ins), parameters) =>
          try {
            val response: String = ins(parameters)
            println(beautyJSON(response))
          } catch {
            case e: Exception => {
              println(s"\nMethod Name: ${name} \nParameters: ${parameters}\nFull Stacktrace: ${e.getCause.getLocalizedMessage}")
            }
          }
      }
    }
  }
}

object ELKRunEngine {
  val ep = new ELKParser
  import ep._
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



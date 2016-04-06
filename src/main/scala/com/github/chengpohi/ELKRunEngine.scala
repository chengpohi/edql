package com.github.chengpohi

import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.{ELK, ELKParser}
import com.github.chengpohi.registry.ELKCommandRegistry

import scala.concurrent.Await
import scala.io.Source
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by chengpohi on 1/3/16.
  */
class ELKRunEngine(env: {val elkParser: ELKParser; val responseGenerator: ResponseGenerator}) {

  import env.elkParser._
  import env.responseGenerator._

  def runInstruments(instruments: Seq[ELK.Instrument], variables: Map[String, String]): Unit = {
    instruments.foreach {
      case i: ELK.Instrument => i.value match {
        case (name, Some(ins), parameters) =>
          try {
            val response: String = Await.result(ins(parameters), Duration.Inf)
            println(beautyJSON(response))
          } catch {
            case e: Exception => {
              println(s"\nMethod Name: ${name} \nParameters: ${parameters}\nFull Stacktrace: ${e.toString}")
            }
          }
      }
    }
  }

  def run(source: String): Unit = {
    val parsed = elkParser.parse(source)
    val (functions, instruments) = generateAST(parsed)
    runInstruments(instruments, Map())
  }
}

object ELKRunEngine {
  private val runEngine: ELKRunEngine = new ELKRunEngine(ELKCommandRegistry)

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage elk file.elk")
      System.exit(0)
    }
    val parseFile: String = Source.fromFile(args(0)).getLines().filter(!_.trim().startsWith("//")).toList.mkString("")
    runEngine.run(parseFile)
  }
}


package com.github.chengpohi

import com.github.chengpohi.helper.ResponseGenerator
import com.github.chengpohi.parser.{ELK, ELKParser}
import com.github.chengpohi.registry.ELKCommandRegistry

import scala.concurrent.Await
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by chengpohi on 1/3/16.
  */
class ELKRunEngine(env: {val elkParser: ELKParser; val responseGenerator: ResponseGenerator}) {

  import env.elkParser._
  import env.responseGenerator._

  def runInstruments(instruments: Seq[ELK.Instrument], variables: Map[String, String]): String = {
    val outputs = instruments.map {
      case i: ELK.Instrument => i.value match {
        case (name, Some(ins), parameters) =>
          try {
            val response: String = Await.result(ins(parameters), Duration.Inf)
            Try(beautyJSON(response)).getOrElse(response)
          } catch {
            case e: Exception => {
              s"\nMethod Name: $name \nParameters: $parameters\nFull Stacktrace: ${e.toString}"
            }
          }
        case (_, None, _) =>
          s"Unknown Instrument!"
      }
    }

    outputs.mkString("\\n")
  }

  def run(source: String): String = {
    val parsed = elkParser.parse(source)
    val (functions, instruments) = generateAST(parsed)
    runInstruments(instruments, Map())
  }
  def run(str: String, parameters: String*): String = {
    val s = String.format(str, parameters: _*)
    run(s)
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
    println(runEngine.run(parseFile))
  }
}


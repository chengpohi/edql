package com.github.chengpohi.parser

import com.github.chengpohi.api.dsl.Definition
import com.github.chengpohi.collection.JsonCollection.Val
import com.typesafe.config.ConfigFactory
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticshell
  * Created by chengpohi on 3/24/16.
  */
class ParserUtils {
  implicit val formats = DefaultFormats
  val instrumentations = ConfigFactory.load("instrumentations.json")

  case class ParserErrorDefinition(parameters: Seq[Val]) extends Definition[String] {
    override def execute: Future[String] = {
      Future {
        val (errorMsg, input) = (parameters.head.extract[String], parameters(1).extract[String])
        write(Map(("illegal_input", input), ("caused_by", errorMsg)))
      }
    }

    override def json: String = Await.result(execute, Duration.Inf)
  }

  def error: Seq[Val] => ParserErrorDefinition = parameters => {
    ParserErrorDefinition(parameters)
  }

  def help: Seq[Val] => Future[String] = {
    {
      case Seq(input) =>
        Future {
          val s = input.extract[String]
          val example: String = instrumentations.getConfig(s.trim).getString("example")
          val description: String = instrumentations.getConfig(s.trim).getString("description")
          write(Map(("example", example), ("description", description)))
        }
      case _ =>
        Future {
          "I have no idea for this."
        }
    }
  }
}

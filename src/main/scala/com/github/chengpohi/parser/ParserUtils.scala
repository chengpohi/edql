package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.Val
import com.typesafe.config.ConfigFactory
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

/**
  * elasticshell
  * Created by chengpohi on 3/24/16.
  */
class ParserUtils {
  implicit val formats = DefaultFormats
  val instrumentations = ConfigFactory.load("instrumentations.json")
  def error(parameters: Seq[Val]): String = {
    val (errorMsg, input) = (parameters.head.extract[String], parameters(1).extract[String])
    write(Map(("illegal_input", input), ("caused_by", errorMsg)))
  }

  def help(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(input) =>
        val s = input.extract[String]
        val example: String = instrumentations.getConfig(s.trim).getString("example")
        val description: String = instrumentations.getConfig(s.trim).getString("description")
        write(Map(("example", example), ("description", description)))
      case _ =>
        "I have no idea for this."
    }
  }
}

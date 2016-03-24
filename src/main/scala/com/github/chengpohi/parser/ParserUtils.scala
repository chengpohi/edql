package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.Val
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

/**
  * elasticshell
  * Created by chengpohi on 3/24/16.
  */
object ParserUtils {
  implicit val formats = DefaultFormats

  def error(parameters: Seq[Val]): String = {
    val (errorMsg, input) = (parameters.head.extract[String], parameters(1).extract[String])
    write(Map(("illegal_input", input), ("caused_by", errorMsg)))
  }
}

package com.github.chengpohi.dsl.serializer

import org.json4s._
import org.json4s.jackson.JsonMethods.{compact, render, _}
import org.json4s.jackson.Serialization.write

import scala.util.Try

trait JSONOps {
  DefaultFormats.preservingEmptyValues
  implicit val formats = DefaultFormats + new NumberSerializer ++ JavaTimeSerializers.defaults

  def toJson[T](t: T): String = {
    val decompose = Extraction.decompose(t)
    val res = decompose.removeField {
      case (_, JString(""))    => true
      case (_, JArray(List())) => true
      case _                   => false
    }
    compact(render(res))
  }

  def beautyJSON(json: String): String = pretty(render(parse(json)))

  def extractJSON(json: String, filterName: String): String = {
    val jObj = parse(json)
    val result = filterName.split("\\.").foldLeft(jObj) { (o, i) =>
      o \ i
    }
    write(result)
  }

  implicit class StringJSONOps(str: String) {
    def beautify: String = {
      if (str.isEmpty) {
        return ""
      }
      Try(beautyJSON(str)).getOrElse(str)
    }
  }

  class NumberSerializer
      extends CustomSerializer[Int](_ =>
        ({
          case JInt(x)    => x.toInt
          case JString(x) => x.toInt
        }, {
          case x: Int => JInt(x)
        }))

}

package com.github.chengpohi.helper

import org.json4s.JValue
import org.json4s.JsonAST.JNothing
import org.json4s.native.JsonMethods
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.io.Source

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */
trait EQLTestTrait
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with JsonMethods {

  implicit val elkParser = EQLTestClient$.elkParser
  val eql = EQLTestClient$.eql

  import eql._

  before {
    EQL {
      delete index ALL_INDEX
    }.await
    EQL {
      create index "test-parser-name"
    }.await
    EQL {
      create index ".eql"
    }.await
    EQL {
      create index "testindex"
    }.await
    EQL {
      refresh index "testindex"
    }.await
  }

  after {
    EQL {
      delete index ALL_INDEX
    }.await
  }

  def equalToJSONFile(str: String) = new JSONAssertWord(str)

  final class JSONAssertWord(jsonFile: String) extends Matcher[String] {
    val expectJSONObj: JValue = parse(
      Source
        .fromInputStream(this.getClass.getResourceAsStream("/json/" + jsonFile))
        .getLines()
        .mkString(""))
    val prettyJSON: String = pretty(render(expectJSONObj))
    def apply(left: String): MatchResult = {
      val diff = parse(left) diff expectJSONObj
      MatchResult(
        diff.changed == JNothing,
        s"$left not equal to $prettyJSON",
        s"$left not equal to $prettyJSON"
      )
    }
  }

}

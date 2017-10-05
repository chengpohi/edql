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
trait ELKTestTrait
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with JsonMethods {

  implicit val elkParser = ELKDSLTestClient.elkParser
  val elasticdsl = ELKDSLTestClient.dsl

  import elasticdsl._

  before {
    DSL {
      delete index ALL_INDEX
    }.await
    DSL {
      create index "test-parser-name"
    }.await
    DSL {
      create index ".elasticdsl"
    }.await
    DSL {
      create index "testindex"
    }.await
    DSL {
      refresh index "testindex"
    }.await
  }

  after {
    DSL {
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

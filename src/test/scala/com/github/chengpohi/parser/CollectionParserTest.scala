package com.github.chengpohi.parser

import fastparse.core.Parsed
import org.scalatest.{FlatSpec, BeforeAndAfter}

import scala.collection.mutable.ArrayBuffer

/**
 * elasticshell
 * Created by chengpohi on 2/1/16.
 */
class CollectionParserTest extends FlatSpec with BeforeAndAfter {
  val collectionParser = new CollectionParser

  import collectionParser._

  "collection" should "parse tuple" in {
    val Parsed.Success(value, _) = collection.parse("(1,2,3)")
    assert(value === ArrayBuffer("1", "2", "3"))
  }

  "collection" should "parse array" in {
    val Parsed.Success(value, _) = collection.parse("[(1,2,3),(4,5,6)]")
    assert(value === ArrayBuffer(ArrayBuffer("1", "2", "3"), ArrayBuffer("4", "5", "6")))
  }
}

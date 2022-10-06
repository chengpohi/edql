package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class JsonParserTest extends AnyFlatSpec with should.Matchers {
  val collectionParser = new JsonParser

  it should "convert obj to json" in {
    val obj = JsonCollection.Obj((JsonCollection.Str("hello"), JsonCollection.Obj()))
    println(obj.toJson)
    assert(obj.toJson.contains("hello"))
  }
}

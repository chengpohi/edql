package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.{Arr, Num, Obj, Str}
import fastparse.core.Parsed
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * elasticeql
  * Created by chengpohi on 2/1/16.
  */
class CollectionParserTest extends FlatSpec with BeforeAndAfter {
  val collectionParser = new CollectionParser

  import collectionParser._

  "collection" should "parse tuple" in {
    val Parsed.Success(value1, _) = jsonExpr.parse("(1, 2 ,3 )")
    assert(value1 === Arr(Num(1.0), Num(2.0), Num(3.0)))
  }

  "collection" should "parse array" in {
    val Parsed.Success(value, _) = jsonExpr.parse("[ (1,2,3), (4,5,6) ]")
    val Parsed.Success(value2, _) = jsonExpr.parse("[(1, 2, 3), 7, 6]")
    val Parsed.Success(value3, _) = jsonExpr.parse("""[("1", "2", 3), 7, 6]""")

    assert(
      value === Arr(Arr(Num(1.0), Num(2.0), Num(3.0)),
                    Arr(Num(4.0), Num(5.0), Num(6.0))))
    assert(
      value2 === Arr(Arr(Num(1.0), Num(2.0), Num(3.0)), Num(7.0), Num(6.0)))
    assert(
      value3 === Arr(Arr(Str("1"), Str("2"), Num(3.0)), Num(7.0), Num(6.0)))
  }

  "collection" should "parse json" in {
    val Parsed.Success(value, _) = jsonExpr.parse("""{ "name":[1,2,3,4]}""")
    val Parsed.Success(value1, _) =
      jsonExpr.parse("""{ "user": {"name":"123","age":23}}""")

    assert(value === Obj(("name", Arr(Num(1.0), Num(2.0), Num(3.0), Num(4.0)))))
    assert(
      value1 === Obj(("user", Obj(("name", Str("123")), ("age", Num(23.0))))))
  }

  "jVal get" should "get field by name" in {
    val Parsed.Success(value, _) = jsonExpr.parse("""{
        |"user":{
        |"name" : "\\s+" , "age":23
        |}
        |}""".stripMargin)
    assert(value.get("user").get("name") === Str("\\s+"))
    assert(value.get("user").get("age") === Num(23))
    val Parsed.Success(value2, _) =
      jsonExpr.parse("""{"name" : "hello\n\nworld", "ppp":"fff"}""".stripMargin)
    assert(value2.get("name").get === Str("hello\n\nworld"))
  }

  "json4s" should "parse array to json" in {
    val Parsed.Success(value, _) = jsonExpr.parse("""[1, 2, 3,4]""")
    val list1 = value.extract[List[Int]]
    assert(list1 === List(1, 2, 3, 4))

    val Parsed.Success(value1, _) = jsonExpr.parse("""["foo", "bar"]""")
    val list2 = value1.extract[List[String]]
    assert(list2 === List("foo", "bar"))

    val Parsed.Success(value2, _) = jsonExpr.parse("""{"age":[1,2,3,4]}""")
    assert(value.toJson === """[1.0,2.0,3.0,4.0]""")
    assert(value1.toJson === """["foo","bar"]""")

    assert(value2.toJson === """{"age":[1.0,2.0,3.0,4.0]}""")
  }

}

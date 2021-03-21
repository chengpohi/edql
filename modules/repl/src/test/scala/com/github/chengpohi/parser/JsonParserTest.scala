package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection.{Arr, Num, Obj, Str}
import fastparse.{Parsed, _}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec

class JsonParserTest extends AnyFlatSpec with BeforeAndAfter {
  val collectionParser = new JsonParser

  import collectionParser._

  "collection" should "parse tuple" in {
    val Parsed.Success(value1, _) = parse("(1, 2 ,3 )", jsonExpr(_))
    assert(value1 === Arr(Num(1.0), Num(2.0), Num(3.0)))
  }

  "collection" should "parse array" in {
    val Parsed.Success(value, _) = parse("[ (1,2,3), (4,5,6) ]", jsonExpr(_))
    val Parsed.Success(value2, _) = parse("[(1, 2, 3), 7, 6]", jsonExpr(_))
    val Parsed.Success(value3, _) = parse("""[("1", "2", 3), 7, 6]""", jsonExpr(_))

    assert(
      value === Arr(Arr(Num(1.0), Num(2.0), Num(3.0)),
        Arr(Num(4.0), Num(5.0), Num(6.0))))
    assert(
      value2 === Arr(Arr(Num(1.0), Num(2.0), Num(3.0)), Num(7.0), Num(6.0)))
    assert(
      value3 === Arr(Arr(Str("1"), Str("2"), Num(3.0)), Num(7.0), Num(6.0)))
  }

  "collection" should "parse json" in {
    val Parsed.Success(value, _) = parse("""{"name":[1,2,3,4]}""", jsonExpr(_))
    val Parsed.Success(value1, _) =
      parse("""{"user": {"name":"123","age":23}}""", jsonExpr(_))

    assert(value === Obj(("name", Arr(Num(1.0), Num(2.0), Num(3.0), Num(4.0)))))
    assert(
      value1 === Obj(("user", Obj(("name", Str("123")), ("age", Num(23.0))))))
  }

  "jVal get" should "get field by name" in {
    val Parsed.Success(value, _) = parse(
      """{
        |"user":{
        |"name" : "\\s+" , "age":23
        |}
        |}""".stripMargin, jsonExpr(_))
    assert(value.get("user").get("name") === Str("\\s+"))
    assert(value.get("user").get("age") === Num(23))
    val Parsed.Success(value2, _) =
      parse("""{"name" : "hello\n\nworld", "ppp":"fff"}""".stripMargin, jsonExpr(_))
    assert(value2.get("name").get === Str("hello\n\nworld"))
  }

  "json4s" should "parse array to json" in {
    val Parsed.Success(value, _) = parse("""[1, 2, 3,4]""", jsonExpr(_))
    val list1 = value.extract[List[Int]]
    assert(list1 === List(1, 2, 3, 4))

    val Parsed.Success(value1, _) = parse("""["foo", "bar"]""", jsonExpr(_))
    val list2 = value1.extract[List[String]]
    assert(list2 === List("foo", "bar"))

    val Parsed.Success(value2, _) = parse("""{"age":[1,2,3,4]}""", jsonExpr(_))
    assert(value.toJson === """[1.0,2.0,3.0,4.0]""")
    assert(value1.toJson === """["foo","bar"]""")

    assert(value2.toJson === """{"age":[1.0,2.0,3.0,4.0]}""")
  }

}

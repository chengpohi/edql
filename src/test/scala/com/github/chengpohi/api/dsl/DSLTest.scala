package com.github.chengpohi.api.dsl

import java.util

import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.search.sort.SortOrder
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

/**
  * elasticdsl
  * Created by chengpohi on 9/22/16.
  */
case class TestMap(id: Int,
                   hello: String,
                   foo: String,
                   name: String,
                   pp: Option[String],
                   tt: Option[Int],
                   list: List[String])

case class TestMapScore(id: String,
                        hello: String,
                        foo: String,
                        name: String,
                        pp: Option[String],
                        tt: Option[Int],
                        list: List[String],
                        score: Long)

class DSLTest
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with ELKCommandTestRegistry {

  import elasticdsl._

  before {
    DSL {
      create index "testindex"
    }.await
    DSL {
      refresh index "testindex"
    }.await
  }

  it should "parse response to json" in {
    val res = DSL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar"))
    }.await
    res.toJson.isEmpty should be(false)
  }

  it should "scroll search to json" in {
    DSL {
      index into "testindex" / "testmap" doc List(
        Map("Hello" -> List("world", "foobar")),
        Map("Hello" -> List("world", "foobar")),
        Map("Hello" -> List("world", "foobar")))
    }.await

    DSL {
      refresh index "testindex"
    }.await

    val res = DSL {
      search in "testindex" / "testmap" size 1 scroll "1m"
    }.await

    val result = res.take(5).toList
    result.size should be(3)
  }

  it should "index nest map" in {
    DSL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar"))
    }.await

    DSL {
      refresh index "testindex"
    }.await
    val result = DSL {
      search in "testindex" / "testmap"
    }.await
    val source: util.ArrayList[String] = result.getHits
      .getAt(0)
      .getSource
      .get("Hello")
      .asInstanceOf[util.ArrayList[String]]
    assert(source.size() === 2)
    assert(source.get(0) === "world")
    assert(source.get(1) === "foobar")
  }

  it should "get doc by id" in {
    val _id: String = "1234"

    DSL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar")) id _id
    }

    DSL {
      refresh index "testindex"
    }.await

    val result1 = DSL {
      search in "testindex" / "testmap" where id equal _id
    }.await

    val result2 = DSL {
      search in "testindex" / "testmap" where id equal "7893"
    }.await

    result1.getId should be(_id)
    result2.isExists should be(false)
  }

  it should "extract json by path" in {
    val _id: String = "1234"

    DSL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar")) id _id
    }

    DSL {
      refresh index "testindex"
    }.await

    val result1 = DSL {
      search in "testindex" / "testmap" where id equal _id extract "_source.Hello"
    }.await
    result1 should be("""["world","foobar"]""")
  }

  "dsl" should "extract by type" in {
    val _id: String = "1234"

    DSL {
      index into "testindex" / "testmap" doc Map(
        "hello" -> "world",
        "foo" -> "bar",
        "name" -> "chengpohi",
        "pp" -> "jack",
        "list" -> List("world", "foobar")) id _id
    }

    DSL {
      index into "testindex" / "testmap" doc Map(
        "hello" -> "world",
        "foo" -> "bar",
        "name" -> "chengpohi") id "5678"
    }

    DSL {
      refresh index "testindex"
    }.await

    val r1 = DSL {
      search in "testindex" / "testmap" where id equal _id
    }.await.as[TestMap]
    r1.head should be(
      TestMap(_id.toInt,
              "world",
              "bar",
              "chengpohi",
              Some("jack"),
              None,
              List("world", "foobar")))

    val r2 = DSL {
      search in "testindex" / "testmap"
    }.await.as[TestMap].toList
    r2.size should be(2)
    r2 should contain(
      TestMap(_id.toInt,
              "world",
              "bar",
              "chengpohi",
              Some("jack"),
              None,
              List("world", "foobar")))
    r2 should contain(
      TestMap(5678, "world", "bar", "chengpohi", None, None, List()))
  }

  "dsl" should "select order by" in {
    DSL {
      index into "testindex" / "testmap" doc List(
        Map("score" -> 1,
            "hello" -> "world",
            "foo" -> "bar",
            "name" -> "chengpohi",
            "pp" -> "jack",
            "list" -> List("world", "foobar")),
        Map("score" -> 2,
            "hello" -> "world",
            "foo" -> "bar",
            "name" -> "chengpohi"),
        Map("score" -> 3,
            "hello" -> "world",
            "foo" -> "bar",
            "name" -> "chengpohi")
      )
    }

    DSL {
      refresh index "testindex"
    }.await

    val r1 = DSL {
      search in "testindex" / "testmap" query ("score" gt "1") sort ("score" as SortOrder.DESC) scroll "1m"
    }.await.as[TestMapScore]
    r1.size should be(2)
    r1.head.score should be(3)
    r1.last.score should be(2)
  }

  "dsl" should "analysis words by tokenizer" in {
    val result = DSL {
      analyze text "hello world" tokenizer "whitespace"
    }.await
    result.getTokens.size() should be(2)
    result.getTokens.get(0).getTerm should be("hello")
    result.getTokens.get(1).getTerm should be("world")
  }

  "dsl" should "update collection" in {
    val _id = "123"
    val indexData = Map("score" -> List("hello", "world"))
    DSL {
      index into "testindex" / "testmap" doc indexData id _id
    }
    DSL {
      refresh index "testindex"
    }.await

    DSL {
      update id _id in "testindex" / "testmap" doc Map("score" -> List(1, 2))
    }.await

    DSL {
      refresh index "testindex"
    }.await

    val result = DSL {
      search in "testindex" / "testmap" where id equal _id
    }.await

    result.getSource.get("score").getClass should be(
      classOf[java.util.ArrayList[_]])
  }

  "dsl" should "index doc with dynamic mapping" in {
    val _id = "123"
    val indexData = Map("score" -> BigInt(123))
    DSL {
      index into "testindex" / "testmap" doc indexData id _id
    }

    DSL {
      refresh index "testindex"
    }.await

    val mapping = DSL {
      get mapping "testindex"
    }.await.toJson
    println(mapping)
  }

  "dsl" should "create index with mapping" in {
    DSL {
      create index "test" analyzers {
        List(
          create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List(
            "lowercase",
            "type_as_payload")
        )
      } fields List(
        create field "text" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer",
        create field "fullname" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store false analyzer "fulltext_analyzer"
      )
    }.await

    val res2 = DSL {
      get mapping "test"
    }.await.toJson
    res2.contains("fulltext_analyzer") should be(true)
  }

  "dsl" should "get term frequency" in {
    DSL {
      create index "test" analyzers List(
        create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List(
          "lowercase",
          "type_as_payload")
      ) fields List(
        create field "text" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer",
        create field "fullname" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store false analyzer "fulltext_analyzer"
      )
    }.await

    DSL {
      index into "test" / "tweet" doc List(
        Map("text" -> "twitter test test test ",
            "fullname" -> "John Doe",
            "id" -> "1"),
        Map("text" -> "Another twitter test ...",
            "fullname" -> "Jane Doe",
            "id" -> "2")
      )
    }.await

    val response: String = client
      .prepareTermVectors()
      .setIndex("test")
      .setType("tweet")
      .setId("1")
      .setFieldStatistics(true)
      .setPositions(true)
      .setOffsets(true)
      .setTermStatistics(true)
      .execute()
      .toJson
    println(response.toJson)
  }

  it should "join index" in {
    DSL {
      index into "testindex" / "testmap" doc Map("foo" -> "bar",
                                                 "name" -> "hello")
    }.await

    DSL {
      index into "testindex" / "testfoo" doc Map("pp" -> "tt",
                                                 "name" -> "hello")
    }.await

    DSL {
      refresh index "testindex"
    }.await

    val res = DSL {
      search in "testindex" / "testmap" join "testindex" / "testfoo" by "name"
    }.await.toJson
    println(res)
  }

  after {
    DSL {
      delete index ALL
    }
  }
}

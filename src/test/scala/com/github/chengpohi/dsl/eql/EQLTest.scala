package com.github.chengpohi.dsl.eql

import java.util

import com.github.chengpohi.helper.EQLTestTrait
import org.elasticsearch.action.termvectors.TermVectorsResponse
import org.elasticsearch.search.sort.SortOrder

import scala.concurrent.Future

/**
  * eql
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

class EQLTest extends EQLTestTrait {

  import eql._

  it should "parse response to json" in {
    val res = EQL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar"))
    }.await.json
    res.isEmpty should be(false)
  }

  it should "scroll search to json" in {
    EQL {
      index into "testindex" / "testmap" doc List(
        Map("Hello" -> List("world", "foobar")),
        Map("Hello" -> List("world", "foobar")),
        Map("Hello" -> List("world", "foobar")))
    }.await

    EQL {
      refresh index "testindex"
    }.await

    val res = EQL {
      search in "testindex" / "testmap" size 1 scroll "1m"
    }.await

    val result = res.take(5).toList
    result.size should be(3)
  }

  it should "index nest map" in {
    EQL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar"))
    }.await

    EQL {
      refresh index "testindex"
    }.await
    val result = EQL {
      search in "testindex" / "testmap"
    }.await
    val source: util.ArrayList[String] = result.getHits
      .getAt(0)
      .getSourceAsMap
      .get("Hello")
      .asInstanceOf[util.ArrayList[String]]
    assert(source.size() === 2)
    assert(source.get(0) === "world")
    assert(source.get(1) === "foobar")
  }

  it should "get doc by id" in {
    val _id: String = "1234"

    EQL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar")) id _id
    }

    EQL {
      refresh index "testindex"
    }.await

    val result1 = EQL {
      search in "testindex" / "testmap" where id equal _id
    }.await

    val result2 = EQL {
      search in "testindex" / "testmap" where id equal "7893"
    }.await

    result1.getId should be(_id)
    result2.isExists should be(false)
  }

  it should "extract json by path" in {
    val _id: String = "1234"

    EQL {
      index into "testindex" / "testmap" doc Map(
        "Hello" -> List("world", "foobar")) id _id
    }

    EQL {
      refresh index "testindex"
    }.await

    val result1 = EQL {
      search in "testindex" / "testmap" where id equal _id extract "_source.Hello"
    }.await
    result1 should be("""["world","foobar"]""")
  }

  "eql" should "extract by type" in {
    val _id: String = "1234"

    EQL {
      index into "testindex" / "testmap" doc Map(
        "hello" -> "world",
        "foo" -> "bar",
        "name" -> "chengpohi",
        "pp" -> "jack",
        "list" -> List("world", "foobar")) id _id
    }

    EQL {
      index into "testindex" / "testmap" doc Map(
        "hello" -> "world",
        "foo" -> "bar",
        "name" -> "chengpohi") id "5678"
    }

    EQL {
      refresh index "testindex"
    }.await

    val r1 = EQL {
      search in "testindex" / "testmap" where id equal _id
    }.as[TestMap].await
    r1.head should be(
      TestMap(_id.toInt,
              "world",
              "bar",
              "chengpohi",
              Some("jack"),
              None,
              List("world", "foobar")))

    val r2 = EQL {
      search in "testindex" / "testmap"
    }.as[TestMap].await.toList
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

  "eql" should "select order by" in {
    EQL {
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

    EQL {
      refresh index "testindex"
    }.await

    val r1 = EQL {
      search in "testindex" / "testmap" query ("score" gt "1") sort ("score" as SortOrder.DESC) scroll "1m"
    }.as[TestMapScore].await
    r1.size should be(2)
    r1.head.score should be(3)
    r1.last.score should be(2)
  }

  "eql" should "analysis words by tokenizer" in {
    val result = EQL {
      analyze text "hello world" tokenizer "whitespace"
    }.await
    result.getTokens.size() should be(2)
    result.getTokens.get(0).getTerm should be("hello")
    result.getTokens.get(1).getTerm should be("world")
  }

  "eql" should "update collection" in {
    val _id = "123"
    val indexData = Map("score" -> List("hello", "world"))
    EQL {
      index into "testindex" / "testmap" doc indexData id _id
    }
    EQL {
      refresh index "testindex"
    }.await

    EQL {
      update id _id in "testindex" / "testmap" doc Map("score" -> List(1, 2))
    }.await

    EQL {
      refresh index "testindex"
    }.await

    val result = EQL {
      search in "testindex" / "testmap" where id equal _id
    }.await

    result.getSource.get("score").getClass should be(
      classOf[java.util.ArrayList[_]])
  }

  "eql" should "index doc with dynamic mapping" in {
    val _id = "123"
    val indexData = Map("score" -> BigInt(123))
    EQL {
      index into "testindex" / "testmap" doc indexData id _id
    }

    EQL {
      refresh index "testindex"
    }.await

    val mapping = EQL {
      get mapping "testindex"
    }.toJson
    println(mapping)
  }

  "eql" should "create index with mapping" in {
    EQL {
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

    val res2 = EQL {
      get mapping "test"
    }.toJson
    res2.contains("fulltext_analyzer") should be(true)
  }

  ignore should "get term frequency" in {
    EQL {
      create index "test" analyzers List(
        create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List(
          "lowercase",
          "type_as_payload")
      ) fields List(
        create field "text" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer",
        create field "fullname" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store false analyzer "fulltext_analyzer"
      )
    }.await

    EQL {
      index into "test" / "tweet" doc List(
        Map("text" -> "twitter test test test ",
            "fullname" -> "John Doe",
            "id" -> "1"),
        Map("text" -> "Another twitter test ...",
            "fullname" -> "Jane Doe",
            "id" -> "2")
      )
    }.await

    val response: Future[TermVectorsResponse] = client
      .prepareTermVectors()
      .setIndex("test")
      .setType("tweet")
      .setId("1")
      .setFieldStatistics(true)
      .setPositions(true)
      .setOffsets(true)
      .setTermStatistics(true)
      .execute()

    println(response.await.json)
  }

  ignore should "join index" in {
    EQL {
      index into "testindex" / "testmap" doc Map("foo" -> "bar",
                                                 "name" -> "hello")
    }.await

    EQL {
      index into "testindex" / "testfoo" doc Map("pp" -> "tt",
                                                 "name" -> "hello")
    }.await

    EQL {
      refresh index "testindex"
    }.await

    val res = EQL {
      search in "testindex" / "testmap" join "testindex" / "testfoo" by "name"
    }.toJson
    println(res)
  }
}

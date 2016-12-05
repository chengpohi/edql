package com.github.chengpohi.api.dsl

import java.util

import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with Matchers with BeforeAndAfter {
  val dsl = ELKCommandTestRegistry.elasticdsl

  import dsl._

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
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await
    res.toJson.isEmpty should be(false)
  }

  it should "scroll search to json" in {
    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await

    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await

    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
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
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await

    DSL {
      refresh index "testindex"
    }.await
    val result = DSL {
      search in "testindex" / "testmap"
    }.await
    val source: util.ArrayList[String] = result.getHits.getAt(0).getSource.get("Hello").asInstanceOf[util.ArrayList[String]]
    assert(source.size() === 2)
    assert(source.get(0) === "world")
    assert(source.get(1) === "foobar")
  }

  it should "get doc by id" in {
    val _id: String = "1234"

    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar")) id _id
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
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar")) id _id
    }

    DSL {
      refresh index "testindex"
    }.await

    val result1 = DSL {
      search in "testindex" / "testmap" where id equal _id extract "_source.Hello"
    }.await
    result1 should be("""["world","foobar"]""")
  }

  after {
    DSL {
      delete index "*"
    }
  }
}

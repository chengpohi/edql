package com.github.chengpohi.api.dsl

import java.util

import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.action.search.SearchResponse
import org.scalatest.{BeforeAndAfter, FlatSpec, ShouldMatchers}

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val dsl = ELKCommandTestRegistry.elasticdsl

  import dsl._

  before {
    DSL {
      delete index "*"
    }.await
    DSL {
      create index "testindex"
    }.await
  }

  it should "parse response to json" in {
    val res = DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await
    res.toJson should not be empty
  }
  it should "scroll search to json" in {
    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await
    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await
    val res = DSL {
      search in "testindex" size 1 scroll "10m"
    }.await
    val result = res.take(5).toList
    result.size should be(2)
  }


  it should "index nest map" in {
    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await

    Thread.sleep(2000)
    val result = DSL {
      search in "testindex"
    }.await
    val source: util.ArrayList[String] = result.getHits.getAt(0).getSource.get("Hello").asInstanceOf[util.ArrayList[String]]
    assert(source.size() === 2)
    assert(source.get(0) === "world")
    assert(source.get(1) === "foobar")
  }

  after {
    DSL {
      delete index "*"
    }.await
  }
}

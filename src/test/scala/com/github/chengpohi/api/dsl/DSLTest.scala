package com.github.chengpohi.api.dsl

import java.util

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.action.search.SearchResponse
import org.scalatest.{BeforeAndAfter, FlatSpec, ShouldMatchers}

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val dsl = new ElasticDSL(ELKCommandTestRegistry.client)

  import dsl._
  import DSLHelper._

  it should "search all empty index" in {
    val result: SearchResponse = DSL {
      search in "*"
    }
    assert(result.getHits.getTotalHits === 0)
  }
  it should "index nest map" in {
    DSL {
      index into "test-index" / "test-map" doc Map("Hello" -> List("world", "foobar"))
    }
    Thread.sleep(3000)
    val result: SearchResponse = DSL {
      search in "test-index"
    }
    val source: util.ArrayList[String] = result.getHits.getAt(0).getSource.get("Hello").asInstanceOf[util.ArrayList[String]]
    assert(source.size() === 2)
    assert(source.get(0) === "world")
    assert(source.get(1) === "foobar")
  }

  after {
    DSL {
      delete index "*"
    }
  }
}

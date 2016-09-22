package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.action.search.SearchResponse
import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with ShouldMatchers {
  val dsl = new ElasticDSL(ELKCommandTestRegistry.client)

  import dsl._

  it should "search all empty index" in {
    val res = DSL {
      search in "*"
    }
    val result: SearchResponse = Await.result(res, Duration.Inf)
    assert(result.getHits.getTotalHits === 0)
  }
}

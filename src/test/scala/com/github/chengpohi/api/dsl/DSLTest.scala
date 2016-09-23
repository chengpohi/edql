package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.action.search.SearchResponse
import org.scalatest.{FlatSpec, ShouldMatchers}

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with ShouldMatchers {
  val dsl = new ElasticDSL(ELKCommandTestRegistry.client)

  import dsl._

  it should "search all empty index" in {
    val result: SearchResponse = DSL {
      search in "*"
    }
    assert(result.getHits.getTotalHits === 0)
  }
}

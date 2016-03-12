package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchResponse, SearchType}

import scala.concurrent.Future


/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticAggs { this: ElasticBase =>
  def aggsSearch(indexName: String, indexType: String, aggsJson: String): Future[RichSearchResponse] = {
    client.execute {
      search in indexName / indexType query "*" aggregations aggsJson searchType SearchType.QueryThenFetch size 0
    }
  }
}

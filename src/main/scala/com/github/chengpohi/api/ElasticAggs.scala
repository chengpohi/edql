package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.AggsDSL
import org.elasticsearch.action.search.SearchResponse

import scala.concurrent.Future


/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticAggs extends AggsDSL{
  import DSLHelper._
  def aggsSearch(indexName: String, indexType: String, aggsJson: String): Future[SearchResponse] = ElasticExecutor {
    aggs count indexName / indexType aggregations aggsJson
  }
}

package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.AggsDSL
import org.elasticsearch.action.search.SearchResponse

import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticAggs extends AggsDSL {

  def aggsSearch(indexName: String, indexType: String, name: String): Future[SearchResponse] = DSL {
    aggs in indexName / indexType avg name
  }

  def termsSearch(indexName: String, indexType: String, name: String): Future[SearchResponse] = DSL {
    aggs in indexName / indexType term name
  }
}

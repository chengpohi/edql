package com.github.chengpohi.api

import org.elasticsearch.action.search.{SearchResponse, SearchType}
import org.elasticsearch.index.query.QueryBuilders

import scala.concurrent.Future


/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticAggs {
  this: ElasticBase =>
  def aggsSearch(indexName: String, indexType: String, aggsJson: String): Future[SearchResponse] = {
    ActionFuture {
      client.client
        .prepareSearch(indexName)
        .setTypes(indexType)
        .setSearchType(SearchType.QUERY_THEN_FETCH)
        .setQuery(QueryBuilders.matchAllQuery())
        .setSize(0)
        .setAggregations(aggsJson.getBytes("UTF-8")).execute
    }
  }
}

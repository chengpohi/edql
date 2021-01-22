package com.github.chengpohi.dsl.eql

import org.elasticsearch.action.search.{SearchRequestBuilder, SearchType}
import org.elasticsearch.index.query.QueryBuilders

/**
  * eql
  * Created by chengpohi on 6/26/16.
  */
trait AggsEQL extends EQLDefinition {
  case object aggs {
    def in(indexPath: IndexPath): SearchRequestDefinition = {
      val searchRequestBuilder: SearchRequestBuilder =
        client.prepareSearch(indexPath.indexName)
      searchRequestBuilder.setTypes(indexPath.indexType)
      searchRequestBuilder.setSize(0)
      searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery())
      searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
      SearchRequestDefinition(searchRequestBuilder)
    }
  }
}

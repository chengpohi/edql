package com.github.chengpohi.api.dsl

import org.elasticsearch.action.search.{SearchRequestBuilder, SearchType}
import org.elasticsearch.index.query.QueryBuilders

/**
  * elasticdsl
  * Created by chengpohi on 6/26/16.
  */
trait AggsDSL extends DSLDefinition {
  case object aggs {
    def in(indexPath: IndexPath): SearchRequestDefinition = {
      val searchRequestBuilder: SearchRequestBuilder = client.prepareSearch(indexPath.indexName)
      searchRequestBuilder.setTypes(indexPath.indexType)
      searchRequestBuilder.setSize(0)
      searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery())
      searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH)
      SearchRequestDefinition(searchRequestBuilder)
    }
  }
}

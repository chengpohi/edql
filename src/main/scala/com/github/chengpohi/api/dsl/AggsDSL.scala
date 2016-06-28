package com.github.chengpohi.api.dsl

import org.elasticsearch.action.search.{SearchRequestBuilder, SearchType}
import org.elasticsearch.index.query.QueryBuilders

/**
  * elasticshell
  * Created by chengpohi on 6/26/16.
  */
trait AggsDSL extends DSLDefinition {
  case object aggs {
    def count(indexPath: IndexPath) = {
      val searchRequestBuilder: SearchRequestBuilder = client.client.prepareSearch(indexPath.indexName)
      searchRequestBuilder.setTypes(indexPath.indexType)
      searchRequestBuilder.setSize(0)
      searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery() )
      searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH )
      SearchRequestDefinition(searchRequestBuilder)
    }
  }
}

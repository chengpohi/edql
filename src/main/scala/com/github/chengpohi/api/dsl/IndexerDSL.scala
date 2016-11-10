package com.github.chengpohi.api.dsl

import org.elasticsearch.action.index.IndexRequestBuilder

/**
  * elasticshell
  * Created by chengpohi on 6/30/16.
  */
trait IndexerDSL extends DSLContext{
  case object index {
    def into(indexPath: IndexPath): IndexRequestDefinition = {
      val indexRequestBuilder: IndexRequestBuilder = client.prepareIndex(indexPath.indexName, indexPath.indexType)
      IndexRequestDefinition(indexRequestBuilder)
    }
  }
}

package com.github.chengpohi.dsl.eql

import org.elasticsearch.action.index.IndexRequestBuilder

/**
  * eql
  * Created by chengpohi on 6/30/16.
  */
trait IndexEQL extends EQLDefinition {
  case object index {
    def into(indexPath: IndexPath): IndexRequestDefinition = {
      val indexRequestBuilder: IndexRequestBuilder =
        client.prepareIndex(indexPath.indexName, indexPath.indexType)
      IndexRequestDefinition(indexRequestBuilder)
    }
  }
}

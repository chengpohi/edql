package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.DeleterDSL
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.delete.DeleteResponse

import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocDeleter extends DeleterDSL {

  def deleteIndex(indexName: String): Future[DeleteIndexResponse] = DSL {
    delete index indexName
  }

  def deleteById(indexName: String, indexType: String, documentId: String): Future[DeleteResponse] = DSL {
    delete in indexName / indexType id documentId
  }
}

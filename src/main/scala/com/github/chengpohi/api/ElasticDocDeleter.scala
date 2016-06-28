package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.DeleterDSL
import org.elasticsearch.action.delete.DeleteResponse

import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocDeleter extends DeleterDSL {

  import DSLHelper._

  def deleteIndex(indexName: String) = ElasticExecutor {
    delete index indexName
  }

  def deleteById(indexName: String, indexType: String, documentId: String): Future[DeleteResponse] = ElasticExecutor {
    delete in indexName / indexType id documentId
  }
}

package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.delete.DeleteResponse

import scala.concurrent.Future

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticDocDeleter {
  this: ElasticBase =>

  def deleteIndex(indexName: String) = client.execute {
    delete index indexName
  }

  def deleteById(indexName: String, indexType: String, documentId: String): Future[DeleteResponse] = client.execute {
    delete id documentId from indexName / indexType
  }

  def createIndex(indexName: String) = client.execute {
    create index indexName
  }
}

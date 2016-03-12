package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticDocDeleter {
  this: ElasticBase =>

  def deleteIndex(indexName: String) = client.execute {
    delete index indexName
  }

  def deleteById(documentId: String, indexName: String, indexType: String): Boolean = client.execute {
    delete id documentId from s"$indexName/$indexType"
  }.await.isFound

  def createIndex(indexName: String) = client.execute {
    create index indexName
  }
}

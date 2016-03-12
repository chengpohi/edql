package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.{DocumentMap, JsonDocumentSource}

import scala.concurrent.Future

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticIndexer {
  this: ElasticBase =>

  def indexMap(indexName: String, indexType: String, docuemntMap: DocumentMap): String = {
    val resp = client.execute {
      index into indexName / indexType doc docuemntMap
    }.await
    resp.getId
  }

  def indexField(indexName: String, indexType: String, fs: Seq[(String, String)]): Future[IndexResult] = client.execute {
    index into indexName / indexType fields fs
  }

  def bulkIndex(indexName: String, indexType: String, fs: Seq[Seq[(String, String)]]): Future[BulkResult] = {
    val bulkIndexes = for (
      f <- fs
    ) yield index into indexName / indexType fields f
    client.execute {
      bulk(bulkIndexes)
    }
  }

  def indexFieldById(indexName: String, indexType: String, uf: Seq[(String, AnyRef)], docId: String): Future[IndexResult] = client.execute {
    index into indexName / indexType fields uf id docId
  }

  def indexJSON(indexName: String, indexType: String, json: String): String = {
    val resp = client.execute {
      index into indexName / indexType doc new JsonDocumentSource(json)
    }.await
    resp.getId
  }

  def indexMapById(indexName: String, indexType: String, specifyId: String, documentMap: DocumentMap): IndexResult = client.execute {
    index into indexName / indexType doc documentMap id specifyId
  }.await

  def indexJSONById(indexName: String, indexType: String, specifyId: String, json: String): IndexResult = client.execute {
    index into indexName / indexType doc new JsonDocumentSource(json) id specifyId
  }.await
}

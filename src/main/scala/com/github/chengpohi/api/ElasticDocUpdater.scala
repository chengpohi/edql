package com.github.chengpohi.api


import com.sksamuel.elastic4s.ElasticDsl._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocUpdater {
  this: ElasticDocQuerier with ElasticBase =>
  def updateAllDocs(indexName: String, indexType: String, uf: Seq[(String, String)]): Future[String] = {
    Future {
      val res = getAllDataByScan(indexName, Some(indexType))
      bulkUpdateField(indexName, res, indexType, uf)
      """{"isFailure": false}"""
    }
  }

  def updateById(indexName: String, indexType: String, uf: Seq[(String, String)], documentId: String) = client.execute {
    update id documentId in indexName / indexType docAsUpsert uf
  }
}

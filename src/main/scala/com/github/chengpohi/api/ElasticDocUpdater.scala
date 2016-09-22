package com.github.chengpohi.api

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocUpdater {
  this: ElasticDocQuerier =>
  import DSLHelper._
  def updateAllDocs(indexName: String, indexType: String, uf: Seq[(String, String)]): Future[String] = {
    Future {
      val res = queryAllByScan(indexName, Some(indexType))
      bulkUpdateField(indexName, res, indexType, uf)
      """{"isFailure": false}"""
    }
  }

  def updateById(indexName: String, indexType: String, uf: Seq[(String, String)], documentId: String) = DSL{
    update id documentId in indexName / indexType docAsUpsert uf
  }
}

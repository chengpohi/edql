package com.github.chengpohi.api

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticDocUpdater{ this: ElasticDocQuerier =>
  def update(indexName: String, indexType: String, uf: Seq[(String, String)]): String = {
    val res = getAllDataByScan(indexName, Some(indexType))
    bulkUpdateField(indexName, res, indexType, uf)
    """{"isFailure": false}"""
  }
}

package com.github.chengpohi.base

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.TypedFieldDefinition
import org.elasticsearch.action.search.SearchResponse

/**
 * elasticshell
 * Created by chengpohi on 1/6/16.
 */
object ElasticCommand extends ElasticBase {
  def mappings(indexName: String, indexType: String, typeDefinitions: Iterable[TypedFieldDefinition]) = {
    client.execute {
      create index indexName mappings (
        indexType as typeDefinitions
        )
    }
  }

  def getMapping(indexName: String) = {
    client.execute {
      get mapping indexName
    }
  }

  def update(indexName: String, indexType: String, uf: (String, String)): String = {
    val res = getAllDataByScan(indexName, Some(indexType))
    bulkUpdateField(indexName, res, indexType, uf)
    s"update $indexName $indexType ${uf._1} with value ${uf._2}"
  }

  def countCommand(indexName: String): String = {
    val resp = client.execute {
      count from indexName
    }.await
    resp.getCount.toString
  }

  def clusterHealth(): String = {
    val resp = client.execute {
      get cluster health
    }.await
    resp.toString
  }

  def reindex(sourceIndex: String, targetIndex: String, sourceIndexType: String, fields: Array[String]): String = {
    val sourceData: Stream[SearchResponse] = getAllDataByScan(sourceIndex)
    bulkCopyIndex(targetIndex, sourceData, sourceIndexType, fields)
    s"reindex from $sourceIndex to $targetIndex"
  }
}

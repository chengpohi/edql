package com.github.chengpohi.base

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{RichSearchResponse, SearchType}
import org.elasticsearch.action.search.SearchResponse

import scala.concurrent.Future

/**
 * elasticshell
 * Created by chengpohi on 1/6/16.
 */
object ElasticCommand extends ElasticBase {

  def mappings(indexName: String, mapping: String) = {
    client.execute {
      create index indexName source mapping
    }
  }

  def getMapping(indexName: String) = {
    client.execute {
      get mapping indexName
    }
  }

  def update(indexName: String, indexType: String, uf: Seq[(String, String)]): String = {
    val res = getAllDataByScan(indexName, Some(indexType))
    bulkUpdateField(indexName, res, indexType, uf)
    """{"isFailure": false}"""
  }

  def countCommand(indexName: String): Future[RichSearchResponse] = client.execute {
    search in indexName size 0
  }

  def clusterHealth(): String = {
    val resp = client.execute {
      get cluster health
    }.await
    resp.toString
  }

  def reindex(sourceIndex: String, targetIndex: String, sourceIndexType: String, fields: Seq[String]): String = {
    val sourceData: Stream[RichSearchResponse] = getAllDataByScan(sourceIndex)
    bulkCopyIndex(targetIndex, sourceData, sourceIndexType, fields)
    """{"hasErrors": false}"""
  }

  def alias(targetIndex: String, sourceIndex: String) = {
    client.execute {
      add alias targetIndex on sourceIndex
    }
  }


  def restoreSnapshot(snapshotName: String, repositoryName: String) = {
    client.execute {
      restore snapshot snapshotName from repositoryName
    }
  }

  def closeIndex(indexName: String) = {
    client.execute {
      close index indexName
    }
  }

  def openIndex(indexName: String) = {
    client.execute {
      open index indexName
    }
  }

  def aggsSearch(indexName: String, indexType: String, aggsJson: String): Future[RichSearchResponse] = {
    client.execute {
      search in indexName / indexType query "*" aggregations aggsJson searchType SearchType.QueryThenFetch size 0
    }
  }
}

package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.SearchType.Scan
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.DocumentMap
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticDocQuerier {
  this: ElasticBase =>

  private val MAX_ALL_NUMBER: Int = 10000

  def getAll(indexName: String, indexType: String): RichSearchResponse = {
    val indexesAndTypes: String = generateSearchIndexesAndTypes(indexName, indexType)
    client.execute {
      search in indexesAndTypes query "*" start 0 limit MAX_ALL_NUMBER
    }.await
  }

  def queryDataByRawQuery(indexName: String, indexType: String, terms: List[(String, String)]): Future[RichSearchResponse] = client.execute {
    search in indexName / indexType query {
      bool {
        must(
          terms.map(termQuery(_))
        )
      }
    }
  }


  def getAllDataByScan(indexName: String, indexType: Option[String] = Some("*")): Stream[RichSearchResponse] = {
    val res = client.execute {
      search in indexName scroll "10m" size 500 searchType Scan
    }

    def fetch(previous: RichSearchResponse) = {
      client.execute {
        search scroll previous.getScrollId
      }
    }

    def toStream(current: Future[RichSearchResponse]): Stream[RichSearchResponse] = {
      val result = Await.result(current, Duration.Inf)
      result.getScrollId match {
        case null => result #:: Stream.empty
        case _ => result #:: toStream(fetch(result))
      }
    }
    toStream(res)
  }

  def bulkCopyIndex(indexName: String, response: Stream[RichSearchResponse], indexType: String, fields: Seq[String]) = {
    response.foreach(r => {
      client.execute {
        bulk(
          r.getHits.getHits.filter(s => s.getType == indexType || indexType == "*").map {
            s => index into indexName / s.getType doc MapSource(s.getSource.asScala.filter(i => fields.contains(i._1)).toMap)
          }: _*
        )
      }
    })
  }


  def reindex(sourceIndex: String, targetIndex: String, sourceIndexType: String, fields: Seq[String]): String = {
    val sourceData: Stream[RichSearchResponse] = getAllDataByScan(sourceIndex)
    bulkCopyIndex(targetIndex, sourceData, sourceIndexType, fields)
    """{"hasErrors": false}"""
  }
  def bulkUpdateField(indexName: String, response: Stream[RichSearchResponse], indexType: String, field: Seq[(String, String)]) = {
    response.foreach(r => {
      client.execute {
        bulk(
          r.getHits.getHits.filter(s => s.getType == indexType || indexType == "*").map {
            s => update(s.getId).in(s"$indexName/$indexType").doc(field)
          }: _*
        )
      }
    })
  }

  def getDocById(indexName: String, indexType: String, docId: String) = client.execute {
    get id docId from s"$indexName/$indexType"
  }

  private[this] def generateSearchIndexesAndTypes(indexName: String, indexType: String): String = indexType match {
    case "*" => indexName
    case s => indexName + "/" + indexType
  }
}

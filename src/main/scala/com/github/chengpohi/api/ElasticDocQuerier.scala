package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._

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

  def queryAll(indexName: String, indexType: String): Future[RichSearchResponse] = {
    val indexesAndTypes: String = buildIndexNameAndIndexType(indexName, indexType)
    client.execute {
      search in indexesAndTypes query "*" start 0 limit MAX_ALL_NUMBER
    }
  }

  def queryDataByRawQuery(indexName: String, indexType: String, terms: List[(String, String)]): Future[RichSearchResponse] = client.execute {
    search in indexName / indexType query {
      bool {
        must(
          terms.map(termQuery(_))
        )
      }
    } start 0 limit MAX_ALL_NUMBER
  }

  def joinQuery(indexName: String, indexType: String, joinIndexName: String, joinIndexType: String, field: String)(implicit maxRetrieveSize: Int = 500) = {
    val joinAll = queryAllByScan(joinIndexName, Some(joinIndexType))(maxRetrieveSize)
    joinAll.flatMap(f => {
      f.hits.map(i => {
        val fieldValue = i.getSource.get(field).asInstanceOf[String]
        queryDataByRawQuery(indexName, indexType, List((field, fieldValue)))
          .map(s => i.sourceAsMap + ("id" -> i.getId) + (s"${indexType}"-> s.hits.map(t => t.sourceAsMap + ("id" -> t.getId))))
      })
    })
  }


  def queryAllByScan(indexName: String, indexType: Option[String] = Some("*"))(implicit maxRetrieveSize: Int = 500): Stream[RichSearchResponse] = {
    val indexNameAndType = buildIndexNameAndIndexType(indexName, indexType.get)
    val res = client.execute {
      search in indexNameAndType scroll "10m" size maxRetrieveSize
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


  def reindex(sourceIndex: String, targetIndex: String, sourceIndexType: String, fields: Seq[String]): Future[String] = Future {
    val sourceData: Stream[RichSearchResponse] = queryAllByScan(sourceIndex)
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

  private[this] def buildIndexNameAndIndexType(indexName: String, indexType: String): String = indexType match {
    case "*" => indexName
    case s => indexName + "/" + indexType
  }
}

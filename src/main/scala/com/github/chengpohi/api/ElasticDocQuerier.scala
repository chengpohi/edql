package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.QueryDSL
import org.elasticsearch.action.search.SearchResponse

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocQuerier extends QueryDSL {
  private val MAX_ALL_NUMBER: Int = 10000

  import DSLHelper._

  def queryAll(indexName: String, indexType: String): Future[SearchResponse] = {
    ElasticExecutor {
      search in indexName / indexType query "*" from 0 size MAX_ALL_NUMBER
    }
  }

  def matchQuery(indexName: String, indexType: String, m: (String, String)): Future[SearchResponse] = {
    ElasticExecutor {
      search in indexName / indexType `match` m from 0 size MAX_ALL_NUMBER
    }
  }
  def termQuery(indexName: String, indexType: String, terms: List[(String, String)]): Future[SearchResponse] = {
    ElasticExecutor {
      search in indexName / indexType must terms from 0 size MAX_ALL_NUMBER
    }
  }

  def joinQuery(indexName: String, indexType: String, joinIndexName: String, joinIndexType: String, field: String)(implicit maxRetrieveSize: Int = 500) = {
    val joinAll = queryAllByScan(joinIndexName, Some(joinIndexType))(maxRetrieveSize)
    joinAll.flatMap(f => {
      f.getHits.asScala.map(i => {
        val fieldValue = i.getSource.get(field).asInstanceOf[String]
        termQuery(indexName, indexType, List((field, fieldValue)))
          .map(s => i.sourceAsMap.asScala + ("id" -> i.getId) + (s"${indexType}" ->
            s.getHits.asScala.map(t => t.sourceAsMap.asScala + ("id" -> t.getId))))
      })
    })
  }

  def queryAllByScan(indexName: String, indexType: Option[String] = Some("*"))(implicit maxRetrieveSize: Int = 500): Stream[SearchResponse] = {
    val res = ElasticExecutor {
      search in indexName / indexType.get scroll "10m" size maxRetrieveSize
    }

    def fetch(previous: SearchResponse) = {
      ElasticExecutor {
        search scroll previous.getScrollId
      }
    }

    def toStream(current: Future[SearchResponse]): Stream[SearchResponse] = {
      val result = Await.result(current, Duration.Inf)
      result.getScrollId match {
        case null => result #:: Stream.empty
        case _ => result #:: toStream(fetch(result))
      }
    }
    toStream(res)
  }

  def bulkCopyIndex(indexName: String, response: Stream[SearchResponse], indexType: String, fields: Seq[String]) = {
    response.foreach(r => {
      r.getHits.getHits.filter(s => s.getType == indexType || indexType == "*").map {
        s => ElasticExecutor {
          index into indexName / s.getType doc s.getSource.asScala.filter(i => fields.contains(i._1)).toMap
        }
      }
    })
  }

  def reindex(sourceIndex: String, targetIndex: String, sourceIndexType: String, fields: Seq[String]): Future[String] = Future {
    val sourceData: Stream[SearchResponse] = queryAllByScan(sourceIndex)
    bulkCopyIndex(targetIndex, sourceData, sourceIndexType, fields)
    """{"hasErrors": false}"""
  }

  def bulkUpdateField(indexName: String, response: Stream[SearchResponse], indexType: String, field: Seq[(String, String)]) = {
    response.foreach(r => {
      r.getHits.getHits.filter(s => s.getType == indexType || indexType == "*").map {
        s => ElasticExecutor {
          update id s.getId doc field in indexName / indexType
        }
      }
    })
  }

  def getDocById(indexName: String, indexType: String, docId: String) = ElasticExecutor {
    get id docId from indexName / indexType
  }
}

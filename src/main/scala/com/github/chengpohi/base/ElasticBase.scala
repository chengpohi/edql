package com.github.chengpohi.base

import com.github.chengpohi.connector.ElasticClientConnector
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.SearchType.Scan
import com.sksamuel.elastic4s.source.{DocumentMap, JsonDocumentSource}
import com.sksamuel.elastic4s._
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * ElasticBase function
 * Created by chengpohi on 6/28/15.
 */
class ElasticBase {
  lazy val client = ElasticClientConnector.client

  private val MAX_ALL_NUMBER: Int = 10000

  def deleteIndex(indexName: String) = client.execute {
    delete index indexName
  }

  def deleteById(documentId: String, indexName: String, indexType: String): Boolean = client.execute {
    delete id documentId from s"$indexName/$indexType"
  }.await.isFound

  def createIndex(indexName: String) = client.execute {
    create index indexName
  }

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

  case class MapSource(source: Map[String, AnyRef]) extends DocumentMap {
    override def map = source
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

  def analysis(analyzer: String, text: String) = Future {
    val request = new AnalyzeRequest()
    request.analyzer(analyzer)
    request.text(text)
    client.admin.indices().analyze(request).get()
  }

  def getDocById(indexName: String, indexType: String, docId: String) = client.execute {
    get id docId from s"$indexName/$indexType"
  }

  def createRepository(repositoryName: String, repositoryType: String, st: Map[String, String]) = client.execute {
    create repository repositoryName `type` repositoryType settings st
  }

  def createSnapshot(snapshotName: String, repositoryName: String) = client.execute {
    create snapshot snapshotName in repositoryName
  }

  def getSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String) = client.execute {
    get snapshot snapshotName from repositoryName
  }

  def getAllSnapshotByRepositoryName(repositoryName: String) = client.execute {
    get snapshot Seq() from repositoryName
  }

  def deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String) = client.execute {
    delete snapshot snapshotName in repositoryName
  }

  private[this] def generateSearchIndexesAndTypes(indexName: String, indexType: String): String = indexType match {
    case "*" => indexName
    case s => indexName + "/" + indexType
  }

}

package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.IndexerDSL
import org.elasticsearch.action.index.IndexResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticIndexer extends IndexerDSL {

  import DSLHelper._

  def indexMap(indexName: String, indexType: String, docuemntMap: Map[String, AnyRef]): String = {
    val resp: Future[IndexResponse] = DSL {
      index into indexName / indexType doc docuemntMap
    }
    Await.result(resp, Duration.Inf).getId
  }

  def indexField(indexName: String, indexType: String, fs: Seq[(String, String)]): Future[IndexResponse] = DSL {
    index into indexName / indexType fields fs
  }

  def bulkIndex(indexName: String, indexType: String, fs: Seq[Seq[(String, String)]]) = {
    fs.foreach(f => {
      DSL {
        index into indexName / indexType fields f
      }
    })
    "true"
  }

  def indexFieldById(indexName: String, indexType: String, uf: Seq[(String, AnyRef)], docId: String): Future[IndexResponse] = DSL {
    index into indexName / indexType fields uf id docId
  }

  def indexJSON(indexName: String, indexType: String, json: String): String = {
    val resp = DSL {
      index into indexName / indexType doc json
    }
    Await.result(resp, Duration.Inf).getId
  }

  def indexMapById(indexName: String, indexType: String, specifyId: String, documentMap: Map[String, AnyRef]): Future[IndexResponse] = DSL {
    index into indexName / indexType doc documentMap id specifyId
  }

  def indexJSONById(indexName: String, indexType: String, specifyId: String, json: String): Future[IndexResponse] = DSL {
    index into indexName / indexType doc json id specifyId
  }
}

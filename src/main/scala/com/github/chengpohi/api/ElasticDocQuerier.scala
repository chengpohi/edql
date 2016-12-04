package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.QueryDSL
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticDocQuerier extends QueryDSL {


  def queryAll(indexName: String, indexType: String): Future[SearchResponse] = {
    DSL {
      search in indexName / indexType query "*" from 0 size MAX_ALL_NUMBER
    }
  }

  def matchQuery(indexName: String, indexType: String, m: (String, String)): Future[SearchResponse] = {
    DSL {
      search in indexName / indexType mth m from 0 size MAX_ALL_NUMBER
    }
  }

  def termQuery(indexName: String, indexType: String, terms: List[(String, String)]): Future[SearchResponse] = {
    DSL {
      search in indexName / indexType must terms from 0 size MAX_ALL_NUMBER
    }
  }

  def joinQuery(indexName: String, indexType: String, joinIndexName: String, joinIndexType: String, field: String)
               (implicit maxRetrieveSize: Int = MAX_RETRIEVE_SIZE): Future[Stream[Map[String, AnyRef]]] = {
    DSL {
      search in indexName / indexType size maxRetrieveSize scroll "10m" join joinIndexName / joinIndexType by field
    }
  }

  def queryAllByScan(indexName: String, indexType: Option[String] = Some("*"))
                    (implicit maxRetrieveSize: Int = MAX_RETRIEVE_SIZE): Stream[SearchHit] = {
    DSL {
      search in indexName / indexType.get size maxRetrieveSize scroll "10m"
    }.await
  }

  def bulkCopyIndex(indexName: String, response: Stream[SearchHit], indexType: String, fields: Seq[String]): Unit = {
    response.filter(s => s.getType == indexType || indexType == "*").foreach(s => {
      DSL {
        index into indexName / s.getType doc s.getSource.asScala.filter(i => fields.contains(i._1)).toMap
      }
    })
  }

  def bulkUpdateField(indexName: String, response: Stream[SearchHit],
                      indexType: String, field: Seq[(String, String)]): Unit = {
    response.filter(s => s.getType == indexType || indexType == "*").map {
      s =>
        DSL {
          update id s.getId doc field in indexName / indexType
        }
    }
  }

  def getDocById(indexName: String, indexType: String, docId: String): Future[GetResponse] = DSL {
    search in indexName / indexType where id equal docId
  }
}

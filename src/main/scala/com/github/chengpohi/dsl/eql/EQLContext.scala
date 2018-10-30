package com.github.chengpohi.dsl.eql

import com.github.chengpohi.parser.collection.JsonCollection.Val
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders, RangeQueryBuilder}
import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilder, SortOrder}

trait EQLContext extends EQLExecutor with FutureOps {

  implicit class IndexNameAndIndexType(indexName: String) {
    def /(indexType: String): IndexPath = {
      IndexPath(indexName, indexType)
    }
  }

  implicit class IndexNameAndIndexTypeVal(indexName: Val) {
    def /(indexType: Val): IndexPath = {
      IndexPath(indexName.extract[String], indexType.extract[String])
    }

    def /(indexType: String): IndexPath = {
      IndexPath(indexName.extract[String], indexType)
    }
  }

  case class IndexPath(indexName: String, indexType: String)

  implicit class StringBuilders(fieldName: String) {
    def gt(v: String): RangeQueryBuilder = {
      new RangeQueryBuilder(fieldName).gt(v)
    }

    def as(o: SortOrder): SortBuilder[_] = {
      new FieldSortBuilder(fieldName).order(o)
    }
  }

  implicit def strToQueryBuilder(query: String): QueryBuilder = query match {
    case "*" => {
      QueryBuilders.matchAllQuery()
    }
    case _ => QueryBuilders.queryStringQuery(query)
  }

  trait OperationStatus

  case object exist extends OperationStatus
}

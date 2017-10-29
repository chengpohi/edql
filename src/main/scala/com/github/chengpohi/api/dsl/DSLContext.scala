package com.github.chengpohi.api.dsl

import com.github.chengpohi.collection.JsonCollection.Val
import com.github.chengpohi.helper.ResponseGenerator
import org.elasticsearch.index.query.{
  QueryBuilder,
  QueryBuilders,
  RangeQueryBuilder
}
import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilder, SortOrder}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by xiachen on 10/11/2016.
  */
trait DSLContext extends FutureOps {
  val responseGenerator = new ResponseGenerator

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

}

trait Definition[A] {
  def execute: Future[A]

  def extract(path: String): ExtractDefinition = {
    ExtractDefinition(this, path)
  }

  def json: String
}

case class ExtractDefinition(definition: Definition[_], path: String)
    extends Definition[String] {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val formats = DefaultFormats

  override def execute: Future[String] = {
    Future {
      val jObj = parse(definition.json)
      val result = path.split("\\.").foldLeft(jObj) { (o, i) =>
        o \ i
      }
      write(result)
    }
  }

  override def json: String = Await.result(execute, Duration.Inf)
}

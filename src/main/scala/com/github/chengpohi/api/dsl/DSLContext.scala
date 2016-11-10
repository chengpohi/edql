package com.github.chengpohi.api.dsl

import com.github.chengpohi.helper.ResponseGenerator
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by xiachen on 10/11/2016.
  */

trait DSLContext extends DSLDefinition {
  val responseGenerator = new ResponseGenerator

  trait Monoid[A] {
    def toJson(a: A): String
  }

  object Monoid {

    implicit object IndexResponseMonoid extends Monoid[IndexResponse] {
      override def toJson(a: IndexResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object DeleteIndexResponseMonoid extends Monoid[DeleteIndexResponse] {
      override def toJson(a: DeleteIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object CreateIndexResponseMonoid extends Monoid[CreateIndexResponse] {
      override def toJson(a: CreateIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object SearchResponseMonoid extends Monoid[SearchResponse] {
      override def toJson(a: SearchResponse): String = responseGenerator.buildXContent(a)
    }

  }

  trait MonoidOp[A] {
    val F: Monoid[A]
    val value: A

    def toJson: String = F.toJson(value)
  }

  implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
    val F = implicitly[Monoid[A]]
    val value = a
  }


  trait FutureToJson[A, Future[_]] {
    val F: Monoid[A]
    val value: A

    def toJson: String = F.toJson(value)

    def await: A = value
  }

  implicit def futureToJson[A: Monoid](a: Future[A]): FutureToJson[A, Future] = new FutureToJson[A, Future] {
    override val F: Monoid[A] = implicitly[Monoid[A]]
    override val value: A = Await.result(a, Duration.Inf)
  }

  implicit class IndexNameAndIndexType(indexName: String) {
    def /(indexType: String): IndexPath = {
      IndexPath(indexName, indexType)
    }
  }

}


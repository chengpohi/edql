package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.converter.ResponseConverter
import com.github.chengpohi.api.serializer.ResponseSerializer
import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.action.support.broadcast.BroadcastResponse
import org.elasticsearch.action.support.master.AcknowledgedResponse
import org.elasticsearch.common.xcontent.ToXContent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait FutureOps
    extends DSLExecutor
    with ResponseSerializer
    with ResponseConverter {

  trait FutureAwaitOps[A] {
    val value: Future[A]
    def await: A = Await.result(value, Duration.Inf)
  }

  implicit def convertToFutureAwaitOps[A](v: Future[A]): FutureAwaitOps[A] =
    new FutureAwaitOps[A] {
      override val value: Future[A] = v
    }

  trait FutureJSONOps[A] {
    val F0: JSONSerializer[A]
    val value: Future[A]

    def toJson: String = {
      val result = Await.result(value, Duration.Inf)
      F0.json(result)
    }
  }

  trait FutureConverterOps[A] {
    val F0: Converter[A]
    val value: Future[A]
    def as[T](implicit mf: Manifest[T]): Future[Stream[T]] =
      value.map(a => {
        F0.as(a)
      })
  }

  implicit def futureToJsonOps[A: JSONSerializer](a: Future[A])(
      implicit F: JSONSerializer[A]): FutureJSONOps[A] =
    new FutureJSONOps[A] {
      override val F0: JSONSerializer[A] = F
      override val value: Future[A] = a
    }

  implicit def futureXContentToJsonOps[B <: ToXContent](a: Future[B])(
      implicit F: JSONSerializer[ToXContent]): FutureJSONOps[ToXContent] =
    new FutureJSONOps[ToXContent] {
      override val F0: JSONSerializer[ToXContent] = F
      override val value: Future[B] = a
    }

  implicit def futureAckToJsonOps[B <: AcknowledgedResponse](a: Future[B])(
      implicit F: JSONSerializer[AcknowledgedResponse])
    : FutureJSONOps[AcknowledgedResponse] =
    new FutureJSONOps[AcknowledgedResponse] {
      override val F0: JSONSerializer[AcknowledgedResponse] = F
      override val value: Future[B] = a
    }

  implicit def futureBroadcastToJsonOps[B <: BroadcastResponse](a: Future[B])(
      implicit F: JSONSerializer[BroadcastResponse])
    : FutureJSONOps[BroadcastResponse] =
    new FutureJSONOps[BroadcastResponse] {
      override val F0: JSONSerializer[BroadcastResponse] = F
      override val value: Future[B] = a
    }

  implicit def futureMixedBroadcastWithToXContentToJsonOps[
      B <: ToXContent with BroadcastResponse](a: Future[B])(
      implicit F: JSONSerializer[ToXContent]): FutureJSONOps[ToXContent] =
    new FutureJSONOps[ToXContent] {
      override val F0: JSONSerializer[ToXContent] = F
      override val value: Future[B] = a
    }

  implicit def futureToConverterOps[A: Converter](a: Future[A])(
      implicit F: Converter[A]): FutureConverterOps[A] =
    new FutureConverterOps[A] {
      override val F0: Converter[A] = F
      override val value: Future[A] = a
    }
}

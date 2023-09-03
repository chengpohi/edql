package com.github.chengpohi.context

import com.github.chengpohi.converter.ResponseConverter
import com.github.chengpohi.serializer.ResponseSerializer

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}

trait FutureOps extends ResponseSerializer with ResponseConverter {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val resultTimeout: Duration = 5000 millis
  trait FutureAwaitOps[A] {
    val value: Future[A]
    def await: A = Await.result(value, resultTimeout)
  }

  implicit def convertToFutureAwaitOps[A](v: Future[A]): FutureAwaitOps[A] =
    new FutureAwaitOps[A] {
      override val value: Future[A] = v
    }

  trait FutureJsonOps[A] {
    val F0: JSONSerializer[A]
    val value: Future[A]

    def toJson: String = {
      val result = Await.result(value, resultTimeout)
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
      implicit F: JSONSerializer[A]): FutureJsonOps[A] =
    new FutureJsonOps[A] {
      override val F0: JSONSerializer[A] = F
      override val value: Future[A] = a
    }

  implicit def futureToConverterOps[A: Converter](a: Future[A])(
      implicit F: Converter[A]): FutureConverterOps[A] =
    new FutureConverterOps[A] {
      override val F0: Converter[A] = F
      override val value: Future[A] = a
    }
}

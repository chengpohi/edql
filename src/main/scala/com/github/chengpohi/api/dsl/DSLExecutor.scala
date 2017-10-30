package com.github.chengpohi.api.dsl

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticdsl
  * Created by chengpohi on 6/28/16.
  */
trait DSLExecutor {
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

  object DSL {
    def apply[A](f: Definition[A]): Future[A] = f.execute
  }

}

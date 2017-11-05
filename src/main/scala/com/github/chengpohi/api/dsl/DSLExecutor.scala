package com.github.chengpohi.api.dsl

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import scalaz._
import Scalaz._

/**
  * elasticdsl
  * Created by chengpohi on 6/28/16.
  */
trait DSLExecutor extends FutureOps {
  trait Definition[A] {
    def execute: Future[A]

    def extract(path: String): ExtractDefinition = {
      ExtractDefinition(this, path)
    }

    def json: String
  }

  case class ExtractDefinition(definition: Definition[_], path: String)
      extends Definition[String] {

    override def execute: Future[String] = {
      val jObj = parse(definition.json)
      val result = path.split("\\.").foldLeft(jObj) { (o, i) =>
        o \ i
      }
      write(result).pure[Future]
    }

    override def json: String = execute.await
  }

  object DSL {
    def apply[A](f: Definition[A]): Future[A] = f.execute
  }

}

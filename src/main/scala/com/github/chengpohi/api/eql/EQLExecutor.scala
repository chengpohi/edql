package com.github.chengpohi.api.eql

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import scalaz.Scalaz._

import scala.concurrent.Future

/**
  * eql
  * Created by chengpohi on 6/28/16.
  */
trait EQLExecutor extends FutureOps {
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

  object EQL {
    def apply[A](f: Definition[A]): Future[A] = f.execute
  }

}

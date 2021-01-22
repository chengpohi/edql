package com.github.chengpohi.dsl.eql

import cats.implicits._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write

import scala.concurrent.Future


trait Definition[A] {
  def execute: Future[A]

  def extract(path: String): ExtractDefinition = {
    ExtractDefinition(this, path)
  }

  def json: String
}


case class ExtractDefinition(definition: Definition[_], path: String)
  extends Definition[String] with FutureOps {


  override def execute: Future[String] = {
    val jObj = parse(definition.json)
    val result = path.split("\\.").foldLeft(jObj) { (o, i) =>
      o \ i
    }
    write(result).pure[Future]
  }

  override def json: String = execute.await
}


case class ErrorHealthRequestDefinition(error: String)
  extends Definition[String] {
  override def execute: Future[String] = Future.failed(new RuntimeException(error))

  override def json: String = error
}


case class PureStringDefinition(s: String) extends Definition[String] {
  override def execute: Future[String] = Future.successful(s)

  override def json: String = s
}

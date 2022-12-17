package com.github.chengpohi.context

import com.github.chengpohi.dsl.EDQLClient
import com.github.chengpohi.dsl.edql._
import com.github.chengpohi.parser.collection.JsonCollection

import scala.collection.mutable

trait Context
  extends Aggs
    with Analyze
    with Delete
    with IndexEDQL
    with Manage
    with QueryEDQL {
  this: EDQLConfig =>
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
  var variables: mutable.Map[String, JsonCollection.Val] = mutable.Map()
  override implicit lazy val eqlClient: EDQLClient = buildClient(config)

  def shutdown: Unit = {
    eqlClient.restClient.close()
  }
}

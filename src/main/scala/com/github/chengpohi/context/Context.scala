package com.github.chengpohi.context

import com.github.chengpohi.edql.parser.json.JsonCollection

import scala.collection.mutable

trait Context
  extends EDQLDefinition {
  this: EDQLConfig =>
  var variables: mutable.Map[String, JsonCollection.Val] = mutable.Map()
  override implicit lazy val eqlClient: EDQLClient = buildClient(config)


  def shutdown: Unit = {
    eqlClient.restClient.close()
  }
}

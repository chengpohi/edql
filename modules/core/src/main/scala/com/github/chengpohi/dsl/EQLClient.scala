package com.github.chengpohi.dsl

import org.elasticsearch.client.RestClient


class EQLClient(rc: RestClient) {
  val restClient: RestClient = rc
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

object EQLClient {
  def apply(rc: RestClient): EQLClient = new EQLClient(rc)
}

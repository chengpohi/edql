package com.github.chengpohi.dsl

import org.elasticsearch.client.RestClient


class EDQLClient(rc: RestClient) {
  val restClient: RestClient = rc
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

object EDQLClient {
  def apply(rc: RestClient): EDQLClient = new EDQLClient(rc)
}

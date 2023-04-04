package com.github.chengpohi.dsl

import org.elasticsearch.client.RestClient


class EDQLClient(rc: RestClient, kb: Boolean = false, ps: String = "") {
  val restClient: RestClient = rc
  val kibanaProxy: Boolean = kb
  val pathPrefix: String = ps
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

object EDQLClient {
  def apply(rc: RestClient, kibanaProxy: Boolean = false, pathPrefix: String = ""): EDQLClient = new EDQLClient(rc, kibanaProxy, pathPrefix)
}

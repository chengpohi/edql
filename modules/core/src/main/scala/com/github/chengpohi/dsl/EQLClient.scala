package com.github.chengpohi.dsl

import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient, RestClient}


class EQLClient(cl: Client, rc: RestClient) {
  val client: Client = cl
  val clusterClient: ClusterAdminClient = client.admin.cluster()
  val indicesClient: IndicesAdminClient = client.admin.indices()
  val restClient: RestClient = rc
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

object EQLClient {
  def apply(cl: Client, rc: RestClient): EQLClient = new EQLClient(cl, rc)
}

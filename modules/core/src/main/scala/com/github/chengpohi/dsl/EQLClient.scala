package com.github.chengpohi.dsl

import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient, RestClient}


class EQLClient(cl: Option[Client], rc: RestClient) {
  val client: Client = cl.orNull
  val clusterClient: ClusterAdminClient = cl.map(_.admin().cluster()).orNull
  val indicesClient: IndicesAdminClient = cl.map(_.admin.indices()).orNull
  val restClient: RestClient = rc
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

object EQLClient {
  def apply(cl: Option[Client], rc: RestClient): EQLClient = new EQLClient(cl, rc)
}

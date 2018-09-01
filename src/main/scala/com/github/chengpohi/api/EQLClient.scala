package com.github.chengpohi.api

import com.github.chengpohi.api.eql._
import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient, RestClient}


trait EQLs
  extends AggsEQL
    with AnalyzeEQL
    with DeleterEQL
    with IndexEQL
    with ManageEQL
    with QueryEQL

class EQLClient(cl: Client, rc: RestClient) extends EQLs {
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

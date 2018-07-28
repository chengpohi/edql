package com.github.chengpohi.api

import com.github.chengpohi.api.eql._
import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient}


trait EQLs
    extends AggsEQL
    with AnalyzeEQL
    with DeleterEQL
    with IndexEQL
    with ManageEQL
    with QueryEQL

class EQLClient(cl: Client) extends EQLs {
  val client: Client = cl
  val clusterClient: ClusterAdminClient = client.admin.cluster()
  val indicesClient: IndicesAdminClient = client.admin.indices()
  val ALL_INDEX: String = "*"
  val ALL_TYPE: String = "_all"
}

package com.github.chengpohi.api

import org.elasticsearch.client.{Client, ClusterAdminClient}

/**
 * elasticshell
 * Created by chengpohi on 1/6/16.
 */
class ElasticDSL(cl: Client) extends ElasticManagement
                      with ElasticIndexer
                      with ElasticDocUpdater
                      with ElasticDocDeleter
                      with ElasticDocQuerier
                      with ElasticAnalyzer
                      with ElasticAggs
                      with ElasticBase{
  val client: Client = cl
  val clusterClient: ClusterAdminClient = client.admin.cluster()
  val indicesClient = client.admin.indices()
}

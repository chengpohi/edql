package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.client.ClusterAdminClient

/**
 * elasticshell
 * Created by chengpohi on 1/6/16.
 */
class ElasticCommand(cl: ElasticClient) extends ElasticManagement
                      with ElasticIndexer
                      with ElasticDocUpdater
                      with ElasticDocDeleter
                      with ElasticDocQuerier
                      with ElasticAnalyzer
                      with ElasticAggs
                      with ElasticBase{
  val client: ElasticClient = cl
  val cluster: ClusterAdminClient = client.admin.cluster()
}

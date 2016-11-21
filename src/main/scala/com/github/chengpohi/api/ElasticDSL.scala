package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.DSLDefinition
import org.elasticsearch.client.{Client, ClusterAdminClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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
                      with ElasticBase with DSLDefinition{
  val client: Client = cl
  val clusterClient: ClusterAdminClient = client.admin.cluster()
  val indicesClient = client.admin.indices()
  implicit def waitFuture[T](r: Future[T]): T = Await.result(r, Duration.Inf)
}

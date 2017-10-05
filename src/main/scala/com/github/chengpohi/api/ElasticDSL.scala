package com.github.chengpohi.api

import com.github.chengpohi.api.dsl._
import org.elasticsearch.client.{Client, ClusterAdminClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticdsl
  * Created by chengpohi on 1/6/16.
  */
trait DSLs
    extends AggsDSL
    with AnalyzeDSL
    with DeleterDSL
    with IndexerDSL
    with ManageDSL
    with QueryDSL

class ElasticDSL(cl: Client) extends DSLs {
  val client: Client = cl
  val clusterClient: ClusterAdminClient = client.admin.cluster()
  val indicesClient = client.admin.indices()
  val ALL_INDEX = "*"

  implicit def waitFuture[T](r: Future[T]): T = Await.result(r, Duration.Inf)
}

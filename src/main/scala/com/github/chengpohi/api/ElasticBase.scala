package com.github.chengpohi.api

import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient}

import scala.concurrent.{Future, Promise}

/**
  * ElasticBase function
  * Created by chengpohi on 6/28/15.
  */
trait ElasticBase {
  val client: Client
  val clusterClient: ClusterAdminClient
  val indicesClient: IndicesAdminClient

  private[this] def buildFuture[A](f: ActionListener[A] => Any): Future[A] = {
    val p = Promise[A]()
    f(new ActionListener[A] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)

      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

  object ActionFuture {
    def apply[A, Q](f: ActionListener[A] => Q): Future[A] = buildFuture(f)
  }

}

package com.github.chengpohi.api

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.DocumentMap
import org.elasticsearch.action.ActionListener
import org.elasticsearch.client.{ClusterAdminClient, IndicesAdminClient}

import scala.concurrent.{Future, Promise}

/**
  * ElasticBase function
  * Created by chengpohi on 6/28/15.
  */
trait ElasticBase {
  val client: ElasticClient
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

  case class MapSource(source: Map[String, AnyRef]) extends DocumentMap {
    override def map = source
  }

  object ActionFuture {
    def apply[A, Q](f: ActionListener[A] => Q): Future[A] = buildFuture(f)
  }

}

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
      def onFailure(e: Exception): Unit = p.tryFailure(e)

      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

  object ActionFuture {
    def apply[A, Q](f: ActionListener[A] => Q): Future[A] = buildFuture(f)
  }

  def toJavaMap[A](m: Map[A, _]): java.util.Map[A, _] = {
    import scala.collection.JavaConverters._
    val res = m.map(a => {
      val r = a._2 match {
        case a: Iterable[_] => a.asJava
        case a => a
      }
      (a._1, r)
    })
    res.asJava
  }
}

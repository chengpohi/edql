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
    val res = m.map(b => {
      val r = b._2 match {
        case a: Iterable[_] => a.asJava
        case a: Int => Integer.parseInt(a.toString)
        case a: Double => java.lang.Double.parseDouble(a.toString)
        case a: Long => java.lang.Long.parseLong(a.toString)
        case a: Float => java.lang.Float.parseFloat(a.toString)
        case a: BigInt => Integer.parseInt(a.toString())
        case a: BigDecimal => a.bigDecimal
        case a => a
      }
      (b._1, r)
    })
    res.asJava
  }
}

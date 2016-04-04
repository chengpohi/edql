package com.github.chengpohi.api

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.source.DocumentMap
import org.elasticsearch.action.ActionListener

import scala.concurrent.{Future, Promise}

/**
 * ElasticBase function
 * Created by chengpohi on 6/28/15.
 */
trait ElasticBase {
  val client: ElasticClient

  def buildFuture[A](f: ActionListener[A] => Any): Future[A] = {
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

}

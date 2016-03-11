package com.github.chengpohi.helper

import org.elasticsearch.action.ActionListener

import scala.concurrent.{Future, Promise}

/**
 * elasticshell
 * Created by chengpohi on 3/11/16.
 */
object FutureResponseHelper {
  def buildFuture[A](f: ActionListener[A] => Any): Future[A] = {
    val p = Promise[A]()
    f(new ActionListener[A] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)
      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

}

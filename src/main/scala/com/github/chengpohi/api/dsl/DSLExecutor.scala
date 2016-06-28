package com.github.chengpohi.api.dsl

import org.elasticsearch.action.ActionListener

import scala.concurrent.{Future, Promise}

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DSLExecutor {
  abstract class ActionRequest[A] {
    def execute: ActionListener[A] => Unit
  }

  def buildFuture[A](f: ActionListener[A] => Any): Future[A] = {
    val p = Promise[A]()
    f(new ActionListener[A] {
      def onFailure(e: Throwable): Unit = p.tryFailure(e)

      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

  object ElasticExecutor {
    def apply[A](f: ActionRequest[A]): Future[A] = buildFuture(f.execute)
  }
}

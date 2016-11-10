package com.github.chengpohi.api.dsl

import org.elasticsearch.action.{ActionListener, ListenableActionFuture}

import scala.concurrent.{Future, Promise}

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DSLExecutor {

  abstract class ActionRequest[A] {
    def execute: Future[A]
  }

  implicit def buildFuture[A](f: ActionListener[A] => Any): Future[A] = {
    val p = Promise[A]()
    f(new ActionListener[A] {
      def onFailure(e: Exception): Unit = p.tryFailure(e)

      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

  implicit def buildFuture[A](f: ListenableActionFuture[A]): Future[A] = {
    val p = Promise[A]()
    f.addListener(new ActionListener[A] {
      def onFailure(e: Exception): Unit = p.tryFailure(e)

      def onResponse(resp: A): Unit = p.trySuccess(resp)
    })
    p.future
  }

  object DSL {
    def apply[A](f: ActionRequest[A]): Future[A] = f.execute
  }

}

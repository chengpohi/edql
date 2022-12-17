package com.github.chengpohi.dsl.edql


import scala.concurrent.Future

/**
 * eql
 * Created by chengpohi on 6/28/16.
 */
trait EDQLExecutor extends FutureOps {

  object EQL {
    def apply[A](f: Definition[A]): Future[A] = f.execute
  }

}

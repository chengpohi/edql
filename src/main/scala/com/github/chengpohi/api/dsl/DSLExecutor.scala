package com.github.chengpohi.api.dsl

import scala.concurrent.Future

/**
  * elasticdsl
  * Created by chengpohi on 6/28/16.
  */
trait DSLExecutor {
  object DSL {
    def apply[A](f: Definition[A]): Future[A] = f.execute
  }
}

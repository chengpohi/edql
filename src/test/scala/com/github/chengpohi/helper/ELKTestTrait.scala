package com.github.chengpohi.helper

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

/**
  * elasticdsl
  * Created by chengpohi on 4/4/16.
  */
trait ELKTestTrait
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with BeforeAndAfterAll {
  implicit val elkParser = ELKDSLTestClient.elkParser
  val elasticdsl = ELKDSLTestClient.dsl

  import elasticdsl._

  before {
    DSL {
      delete index ALL_INDEX
    }.await
    DSL {
      create index "test-parser-name"
    }.await
    DSL {
      create index ".elasticdsl"
    }.await
    DSL {
      create index "testindex"
    }.await
    DSL {
      refresh index "testindex"
    }.await
  }

  after {
    DSL {
      delete index ALL_INDEX
    }.await
  }
}

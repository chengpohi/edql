package com.github.chengpohi.api.dsl

import java.util

import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.scalatest.{BeforeAndAfter, FlatSpec, ShouldMatchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/**
  * elasticshell
  * Created by chengpohi on 9/22/16.
  */
class DSLTest extends FlatSpec with ShouldMatchers with BeforeAndAfter {
  val dsl = ELKCommandTestRegistry.elasticdsl

  import dsl._

  before {
    DSL {
      create index "testindex"
    }
  }

  it should "parse response to json" in {
    val res = DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    }.await
    res.toJson should not be empty
  }


  it should "index nest map" in {
    DSL {
      index into "testindex" / "testmap" doc Map("Hello" -> List("world", "foobar"))
    } andThen {
      case Success(f) =>
        Thread.sleep(3000)
        DSL {
          search in "testindex"
        }.andThen {
          case Success(result) =>
            val source: util.ArrayList[String] = result.getHits.getAt(0).getSource.get("Hello").asInstanceOf[util.ArrayList[String]]
            assert(source.size() === 2)
            assert(source.get(0) === "world")
            assert(source.get(1) === "foobar")
        }
    }
  }

  after {
    DSL {
      delete index "*"
    }
  }
}

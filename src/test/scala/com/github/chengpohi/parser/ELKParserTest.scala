package com.github.chengpohi.parser

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, Paths}

import com.github.chengpohi.ELKRunEngine
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * elasticservice
  * Created by chengpohi on 1/19/16.
  */
class ELKParserTest extends FlatSpec with BeforeAndAfter {
  val outContent = new ByteArrayOutputStream()
  val errContent = new ByteArrayOutputStream()
  val runEngine: ELKRunEngine = new ELKRunEngine(ELKCommandTestRegistry)
  before {
    runEngine.run(s"""create index ".elasticshell"""")
    runEngine.run( """ create index "test-parser-name" """)
    outContent.reset()
  }

  "ELKParser" should "get health of elasticsearch" in {
    Console.withOut(outContent) {
      runEngine.run("health")
    }
    assert(outContent.toString.contains("cluster_name"))
  }

  "ELKParser" should "index doc by indexName, indexType, fields" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" }""")

      Thread.sleep(1000)
      //then
      runEngine.run( """ query "test-parser-name" """)
    }
    assert(outContent.toString.contains(""""name":"hello""""))
  }

  "ELKParser" should "index doc by indexName, indexType, id and fields" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

      Thread.sleep(1000)
      //then
      runEngine.run( """ query "test-parser-name" """)
    }
    assert(outContent.toString().contains(""""_id":"123""""))
  }

  "ELKParser" should "update doc by id and fields" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

      Thread.sleep(1000)

      runEngine.run( """update "test-parser-name" "test-parser-type" { "name" : "chengpohi"} id "123"""")

      Thread.sleep(1000)
      //then
      runEngine.run( """ query "test-parser-name" """)
    }
    assert(outContent.toString().contains(""""name":"chengpohi""""))

  }

  "ELKParser" should "reindex by sourceIndex targetIndex sourceType fields" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} """)
      Thread.sleep(3000)
      runEngine.run( """reindex "test-parser-name" "test-parser-name-reindex" "test-parser-type" ["name"]""")

      Thread.sleep(3000)

      runEngine.run( """ query "test-parser-name-reindex" """)
      runEngine.run( """ delete "test-parser-name-reindex" """)
      //then
    }
    assert(outContent.toString.contains(
      """"name":"hello"""".stripMargin.trim))
  }
  "ELKParser" should "delete doc by id" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

      Thread.sleep(1000)
      //then
      runEngine.run( """delete "test-parser-name" "test-parser-type" "123" """)
      Thread.sleep(1000)
      runEngine.run( """query "test-parser-name" "test-parser-type"""")
    }
    assert(outContent.toString.contains(""""hits":[]"""))
  }

  "ELKParser" should "update doc by indexName indexType tuple" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} """)
      Thread.sleep(1000)
      runEngine.run( """update "test-parser-name" "test-parser-type" {"name":"elasticservice"} """)
      Thread.sleep(3000)
      runEngine.run( """ query "test-parser-name" """)
    }
    assert(outContent.toString.contains(
      """"name"""".stripMargin.trim))
  }

  "ELKParser" should "analysis doc by specific analyzer" in {
    Console.withOut(outContent) {
      runEngine.run( """analysis "standard" "foo,bar"""")
    }
    assert(outContent.toString.contains(
      """"token":"foo","""
    ))
  }

  "ELKParser" should "index and get doc by id" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"}  id "HJJJJJJH"""")
      runEngine.run( """get "test-parser-name" "test-parser-type" "hJJJJJJH"""")
    }
    assert(outContent.toString.contains( """"_id":"hJJJJJJH""""))
  }

  "ELKParser" should "extract json data" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} "HJJJJJJH" """)
      Thread.sleep(2000)
      runEngine.run( """query "test-parser-name" \\ "name" """)
    }
    assert(outContent.toString.contains( """"name":"hello""""))
  }

  "ELKParser" should "query data by json" in {
    Console.withOut(outContent) {
      runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"Hello world", "text": "foo bar"} "HJJJJJJH" """)
      Thread.sleep(2000)
      runEngine.run( """term query "test-parser-name" "test-parser-type" {"name":"hello", "text": "foo"}""")
    }
    assert(outContent.toString.contains("Hello world"))
    assert(outContent.toString.contains("foo bar"))
  }

  "ELKParser" should "set mapping for indexname indextype" in {
    Console.withOut(outContent) {
      runEngine.run(
        """mapping "test-mapping" {
          |  "mappings": {
          |    "bookmark": {
          |      "properties": {
          |        "created_at": {
          |          "type": "date"
          |        },
          |        "name": {
          |          "type": "string",
          |          "index": "not_analyzed"
          |        }
          |      }
          |    }
          |  }
          |}""".stripMargin('|'))
      Thread.sleep(1000)
      runEngine.run( """ "test-mapping" mapping """)
    }
    assert(outContent.toString.contains( """"format":"strict_date_optional_time||epoch_millis""""))
    assert(outContent.toString.contains( """"not_analyzed"""))
    runEngine.run( """ delete "test-mapping"""")
  }

  "ELKParser" should "bulk index docs" in {
    Console.withOut(outContent) {
      runEngine.run(
        """bulk index "test-parser-name" "test-parser-type" [
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23}
          |] """.stripMargin)
      Thread.sleep(2000)
      runEngine.run( """ count "test-parser-name" """)
    }
    assert(outContent.toString.contains("7"))
    assert(outContent.toString.contains("false"))
  }

  "ELKParser" should "index newline" in {
    Console.withOut(outContent) {
      runEngine.run(
        """bulk index "test-parser-name" "test-parser-type" [{"price": 10000, "color": "red"}]""".stripMargin)
      Thread.sleep(2000)
      runEngine.run( """ count "test-parser-name" """)
    }
    assert(outContent.toString.contains(""))
  }
  "ELKParser" should "aggs data" in {
    Console.withOut(outContent) {
      runEngine.run(
        """bulk index "test-parser-name" "test-parser-type" [
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 24},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 25},
          |{"name": "hello","age": 25},
          |{"name": "hello","age": 22},
          |{"name": "hello","age": 22}
          |] """.stripMargin)
      Thread.sleep(2000)
      runEngine.run( """aggs count "test-parser-name" "test-parser-type" {"ages":{"terms": {"field": "age"}}}""")
    }
    assert(outContent.toString.contains( """"key":23.0"""))
  }

  "ELKParser" should "alias index" in {
    Console.withOut(outContent) {
      runEngine.run(
        """bulk index "test-parser-name" "test-parser-type" [
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 24},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 25},
          |{"name": "hello","age": 25},
          |{"name": "hello","age": 22},
          |{"name": "hello","age": 22}
          |] """.stripMargin)
      Thread.sleep(2000)
      runEngine.run( """alias "alias-index" "test-parser-name"""")
      Thread.sleep(2000)
      runEngine.run( """aggs count "alias-index" "test-parser-type" {"ages":{"terms": {"field": "age"}}}""")
    }
    assert(outContent.toString.contains(
      """  "aggregations":{
        |    "ages":{
        |      "doc_count_error_upper_bound":0,""".stripMargin))
  }

  "ELKParser" should "create snapshot" in {
    Console.withOut(outContent) {
      runEngine.run(
        """create repository "test_snapshot" "fs" {"compress": "true", "location": "./target/elkrepo"} """.stripMargin)
      Thread.sleep(2000)

      runEngine.run(
        """create snapshot "snapshot1" "test_snapshot"""".stripMargin)
      Thread.sleep(2000)

      runEngine.run(
        """get snapshot "snapshot1" "test_snapshot"""".stripMargin)

      Thread.sleep(2000)

      runEngine.run(
        """close index "*"""".stripMargin)

      Thread.sleep(2000)

      runEngine.run(
        """restore snapshot "snapshot1" "test_snapshot"""".stripMargin)

      Thread.sleep(2000)

      runEngine.run(
        """close index "*"""".stripMargin)

      Thread.sleep(2000)

      runEngine.run(
        """open index "*"""".stripMargin)

      Thread.sleep(2000)

      runEngine.run(
        """delete snapshot "snapshot1" "test_snapshot"""".stripMargin)

    }
    assert(outContent.toString.contains("\"snapshot\":\"snapshot1\","))
  }

  "ELKParser" should "cluster stats" in {
    Console.withOut(outContent) {
      runEngine.run("cluster stats")
    }
    assert(outContent.toString.contains("cluster_name"))
  }

  "ELKParser" should "node stats" in {
    Console.withOut(outContent) {
      runEngine.run("node stats")
    }
    assert(outContent.toString.contains("heap_used_in_bytes"))
  }
  "ELKParser" should "indices stats" in {
    Console.withOut(outContent) {
      runEngine.run("indices stats")
    }
    assert(outContent.toString.contains("indices"))
  }
  /*"ELKParser" should "retrieve cluster settings" in {
    Console.withOut(outContent) {
      runEngine.run("cluster settings")
    }
    assert(outContent.toString.contains("indices"))
  }*/
  "ELKParser" should "retrieve node settings" in {
    Console.withOut(outContent) {
      runEngine.run("node settings")
    }
    assert(outContent.toString.contains("transport_address"))
  }

  "ELKParser" should "retrieve index settings" in {
    Console.withOut(outContent) {
      runEngine.run( """"test-parser-name" settings""")
    }
    assert(outContent.toString.contains("test-parser-name"))
  }

  "ELKParser" should "retrieve pending tasks" in {
    Console.withOut(outContent) {
      runEngine.run( """pending tasks""")
    }
    assert(outContent.toString.contains( """"tasks""""))
  }

  "ELKParser" should "list all indices" in {
    Console.withOut(outContent) {
      runEngine.run( """cluster state""")
    }
    assert(outContent.toString.contains("indices"))
  }

  "ELKParser" should "wait  for status" in {
    Console.withOut(outContent) {
      runEngine.run(
        """bulk index "test-parser-name" "test-parser-type" [
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23},
          |{"name": "hello","age": 23}
          |] """.stripMargin)
      runEngine.run("""wait for status "YELLOW"""")
    }
    assert(outContent.toString.contains("yellow"))
  }

  "ELKParser" should "list help for command" in {
    Console.withOut(outContent) {
      runEngine.run("create index ?")
    }
    assert(outContent.toString.contains(""""description":"create index by index name""""))
  }

  "ELKParser" should "create analyzer" in {
    Console.withOut(outContent) {
      runEngine.run("""create analyzer {"analyzer":{"myAnalyzer":{"type":"pattern","pattern":"\\s+"}}}""")
    }
    println(outContent.toString)
  }

  after {
    runEngine.run( """ delete "test-parser-name"""")
    runEngine.run( """ delete ".elasticshell"""")
  }
}


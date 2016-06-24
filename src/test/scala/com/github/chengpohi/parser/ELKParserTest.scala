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
    runEngine.run("""create index ".elasticshell"""")
    runEngine.run( """ create index "test-parser-name" """)
    outContent.reset()
  }

  "ELKParser" should "get health of elasticsearch" in {
    val result = runEngine.run("health")
    assert(result.contains("cluster_name"))
  }

  "ELKParser" should "index doc by indexName, indexType, fields" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" }""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """ query "test-parser-name" """)
    assert(result.contains(""""name":"hello""""))
  }

  "ELKParser" should "index doc by indexName, indexType, id and fields" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """ query "test-parser-name" """)
    assert(result.contains(""""_id":"123""""))
  }

  "ELKParser" should "update doc by id and fields" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)

    runEngine.run( """update "test-parser-name" "test-parser-type" { "name" : "chengpohi"} id "123"""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """ query "test-parser-name" """)
    assert(result.contains(""""name":"chengpohi""""))

  }

  "ELKParser" should "reindex by sourceIndex targetIndex sourceType fields" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} """)
    Thread.sleep(3000)
    runEngine.run( """reindex "test-parser-name" "test-parser-name-reindex" "test-parser-type" ["name"]""")

    Thread.sleep(3000)

    val result = runEngine.run( """ query "test-parser-name-reindex" """)
    runEngine.run( """ delete "test-parser-name-reindex" """)
    //then
    assert(result.contains(
      """"name":"hello"""".stripMargin.trim))
  }
  "ELKParser" should "delete doc by id" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)
    //then
    runEngine.run( """delete "test-parser-name" "test-parser-type" "123" """)
    Thread.sleep(1000)
    val result = runEngine.run( """query "test-parser-name" "test-parser-type"""")
    assert(result.contains(""""hits":[]"""))
  }

  "ELKParser" should "update doc by indexName indexType tuple" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} """)
    Thread.sleep(1000)
    runEngine.run( """update "test-parser-name" "test-parser-type" {"name":"elasticservice"} """)
    Thread.sleep(3000)
    val result = runEngine.run( """ query "test-parser-name" """)
    assert(result.contains(
      """"name"""".stripMargin.trim))
  }

  "ELKParser" should "analysis doc by specific analyzer" in {
    val result = runEngine.run( """analysis "standard" "foo,bar"""")
    assert(result.contains(
      """"token":"foo","""
    ))
  }

  "ELKParser" should "index and get doc by id" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"}  id "HJJJJJJH"""")
    val result = runEngine.run( """get "test-parser-name" "test-parser-type" "hJJJJJJH"""")
    assert(result.contains( """"_id":"hJJJJJJH""""))
  }

  "ELKParser" should "extract json data" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"hello"} id "HJJJJJJH" """)
    Thread.sleep(2000)
    val result = runEngine.run( """query "test-parser-name" "test-parser-type" \\ "hits.hits._source.name"""")
    assert(result === """"hello"""")
  }

  "ELKParser" should "query data by json" in {
    runEngine.run( """index "test-parser-name" "test-parser-type" {"name":"Hello world", "text": "foo bar"} id "HJJJJJJH" """)
    Thread.sleep(2000)
    val result = runEngine.run( """term query "test-parser-name" "test-parser-type" {"name":"hello", "text": "foo"}""")
    assert(result.contains("Hello world"))
    assert(result.contains("foo bar"))
  }

  "ELKParser" should "set mapping for indexname indextype" in {
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
    val result = runEngine.run( """ "test-mapping" mapping """)
    assert(result.contains( """"format":"strict_date_optional_time||epoch_millis""""))
    assert(result.contains( """"not_analyzed"""))
  }

  "ELKParser" should "bulk index docs" in {
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
    val result = runEngine.run( """ count "test-parser-name" """)
    assert(result.contains("7"))
    assert(result.contains("false"))
  }

  "ELKParser" should "index newline" in {
    runEngine.run(
      """bulk index "test-parser-name" "test-parser-type" [{"price": 10000, "color": "red"}]""".stripMargin)
    Thread.sleep(2000)
    val result = runEngine.run( """ count "test-parser-name" """)
    assert(result.contains(""))
  }
  "ELKParser" should "aggs data" in {
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
    val result = runEngine.run( """aggs count "test-parser-name" "test-parser-type" {"ages":{"terms": {"field": "age"}}}""")
    println(result)
    assert(result.contains( """"key":23.0"""))
  }

  "ELKParser" should "alias index" in {
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
    val result = runEngine.run( """aggs count "alias-index" "test-parser-type" {"ages":{"terms": {"field": "age"}}}""")
    assert(result.contains(
      """  "aggregations":{
        |    "ages":{
        |      "doc_count_error_upper_bound":0,""".stripMargin))
  }

  "ELKParser" should "create snapshot" in {
    runEngine.run(
      """create repository "test_snapshot" "fs" {"compress": "true", "location": "./target/elkrepo"} """.stripMargin)
    Thread.sleep(2000)

    runEngine.run(
      """create snapshot "snapshot1" "test_snapshot"""".stripMargin)
    Thread.sleep(2000)

    val result = runEngine.run(
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

    assert(result.contains("\"snapshot\":\"snapshot1\","))
  }

  "ELKParser" should "cluster stats" in {
    val result = runEngine.run("cluster stats")
    assert(result.contains("cluster_name"))
  }

  "ELKParser" should "node stats" in {
    val result = runEngine.run("node stats")
    assert(result.contains("heap_used_in_bytes"))
  }
  "ELKParser" should "indices stats" in {
    val result = runEngine.run("indices stats")
    assert(result.contains("indices"))
  }
  /*"ELKParser" should "retrieve cluster settings" in {
    Console.withOut(outContent) {
      runEngine.run("cluster settings")
    }
    assert(result.contains("indices"))
  }*/
  "ELKParser" should "retrieve node settings" in {
    val result = runEngine.run("node settings")
    assert(result.contains("transport_address"))
  }

  "ELKParser" should "retrieve index settings" in {
    val result = runEngine.run( """"test-parser-name" settings""")
    assert(result.contains("test-parser-name"))
  }

  "ELKParser" should "retrieve pending tasks" in {
    val result = runEngine.run( """pending tasks""")
    assert(result.contains( """"tasks""""))
  }

  "ELKParser" should "list all indices" in {
    val result = runEngine.run( """cluster state""")
    assert(result.contains("indices"))
  }

  "ELKParser" should "wait for status" in {
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
    val result = runEngine.run("""wait for status "YELLOW"""")
    assert(result.contains("yellow"))
  }

  "ELKParser" should "list help for command" in {
    val result = runEngine.run("create index ?")
    assert(result.contains(""""description":"create index by index name""""))
  }

  "ELKParser" should "create analyzer" in {
    runEngine.run("""create analyzer {"analyzer":{"myAnalyzer":{"type":"pattern","pattern":"\\s+"}}}""")
    Thread.sleep(1000)
    val result = runEngine.run("""".elasticshell" settings""")
    assert(result.contains("myAnalyzer"))
  }

  "ELKParser" should "join query" in {
    runEngine.run(
      """bulk index "test-index-name-1" "test-index-type-1" [
        |{"tip": "hello","_tip_id": "1"},
        |{"tip": "world","_tip_id": "2"}
        |] """.stripMargin)
    runEngine.run(
      """bulk index "test-index-name" "test-index-type" [
        |{"name": "test","_tip_id": "1"},
        |{"name": "foo","_tip_id": "2"},
        |{"name": "bar","_tip_id": "1"},
        |{"name": "jack","_tip_id": "2"}
        |] """.stripMargin)
    Thread.sleep(1000)
    val result = runEngine.run(
      """
        |query "test-index-name" "test-index-type" join "test-index-name-1" "test-index-type-1" by "_tip_id"
      """.stripMargin)
    Thread.sleep(1000)
    assert(result.contains("test-index-type"))
  }

  after {
    runEngine.run( """ delete "*"""")
  }
}

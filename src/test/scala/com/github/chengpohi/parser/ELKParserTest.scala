package com.github.chengpohi.parser

import java.io.ByteArrayOutputStream

import com.github.chengpohi.ELKInterpreter
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * elasticservice
  * Created by chengpohi on 1/19/16.
  */
class ELKParserTest extends FlatSpec with BeforeAndAfter {
  val outContent = new ByteArrayOutputStream()
  val errContent = new ByteArrayOutputStream()
  val runEngine: ELKInterpreter = new ELKInterpreter(ELKCommandTestRegistry)

  before {
    runEngine.run("""create index ".elasticshell"""")
    runEngine.run( """ create index "test-parser-name" """)
    outContent.reset()
    Thread.sleep(1000)
  }

  "ELKParser" should "get health of elasticsearch" in {
    val result = runEngine.run("health")
    assert(result.contains("cluster_name"))
  }

  "ELKParser" should "index doc by indexName, indexType, fields" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" }""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """search in "test-parser-name" """)
    assert(result.contains(""""name":"hello""""))
  }

  "ELKParser" should "index doc by indexName, indexType, id and fields" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """ search in "test-parser-name" """)
    assert(result.contains(""""_id":"123""""))
  }

  "ELKParser" should "update doc by id and fields" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)

    runEngine.run( """update on "test-parser-name" / "test-parser-type" fields { "name" : "chengpohi"} id "123"""")

    Thread.sleep(1000)
    //then
    val result = runEngine.run( """ search in "test-parser-name" """)
    assert(result.contains(""""name":"chengpohi""""))
  }

  "ELKParser" should "reindex by sourceIndex targetIndex sourceType fields" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} """)
    Thread.sleep(3000)
    runEngine.run( """reindex into "test-parser-name" / "test-parser-name-reindex" from "test-parser-type" fields ["name"]""")

    Thread.sleep(3000)

    val result = runEngine.run( """ search in "test-parser-name-reindex" """)
    runEngine.run( """ delete index "test-parser-name-reindex" """)
    //then
    assert(result.contains(
      """"name":"hello"""".stripMargin.trim))
  }
  "ELKParser" should "delete doc by id" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    Thread.sleep(1000)
    //then
    runEngine.run( """delete from "test-parser-name" / "test-parser-type" id "123" """)
    Thread.sleep(1000)
    val result = runEngine.run( """search in "test-parser-name" / "test-parser-type"""")
    assert(result.contains(""""hits":[]"""))
  }

  "ELKParser" should "update doc by indexName indexType tuple" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} """)
    Thread.sleep(1000)
    runEngine.run( """update on "test-parser-name" / "test-parser-type" fields {"name":"elasticservice"} """)
    Thread.sleep(3000)
    val result = runEngine.run( """ search in "test-parser-name" """)
    assert(result.contains(
      """"name"""".stripMargin.trim))
  }

  "ELKParser" should "analysis doc by specific analyzer" in {
    val result = runEngine.run( """analysis "foo,bar" by "standard"""")
    assert(result.contains(
      """"token":"foo","""
    ))
  }

  "ELKParser" should "index and get doc by id" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"}  id "HJJJJJJH"""")
    val result = runEngine.run( """get from "test-parser-name" / "test-parser-type" id "hJJJJJJH"""")
    assert(result.contains( """"_id":"hJJJJJJH""""))
  }

  "ELKParser" should "extract json data" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} id "HJJJJJJH" """)
    Thread.sleep(2000)
    val result = runEngine.run( """search in "test-parser-name" / "test-parser-type" \\ "hits.hits._source.name"""")
    assert(result === """"hello"""")
  }

  "ELKParser" should "search data by json" in {
    runEngine.run( """index into "test-parser-name" / "test-parser-type" fields {"name":"Hello world", "text": "foo bar"} id "HJJJJJJH" """)
    Thread.sleep(2000)
    val result = runEngine.run( """term query "test-parser-name" "test-parser-type" {"name":"hello", "text": "foo"}""")
    assert(result.contains("Hello world"))
    assert(result.contains("foo bar"))
  }

  "ELKParser" should "set mapping for indexname indextype" in {
    val r = runEngine.run(
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
    val result = runEngine.run( """"test-mapping" mapping""")
    assert(result.contains( """"type":"date""""))
    assert(result.contains( """"keyword"""))
  }


  "ELKParser" should "update mapping" in {
    runEngine.run(
      """update mapping "test-parser-name" "bookmark" {
        |      "properties": {
        |        "created_at": {
        |          "type": "date"
        |        },
        |        "name": {
        |          "type": "string",
        |          "index": "not_analyzed"
        |        }
        |      }
        |}""".stripMargin('|'))
    Thread.sleep(2000)
    val result = runEngine.run(""""test-parser-name" mapping \\ "test-parser-name.mappings.bookmark.properties.name.type" """)
    assert(result === "\"keyword\"")
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
  "ELKParser" should "aggs avg" in {
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
    val result = runEngine.run( """aggs in "test-parser-name" / "test-parser-type" avg "age"""")
    assert(result.contains( """23"""))
  }

  "ELKParser" should "aggs terms" in {
    val p=  runEngine.run(
      """mapping "test-index2" {
        |  "mappings": {
        |    "test-parser-type": {
        |      "properties": {
        |        "title": {
        |          "type": "string",
        |          "analyzer": "english",
        |          "fields": {
        |            "tags": {
        |             "type": "string",
        |             "analyzer": "english",
        |             "fielddata": {
        |               "format": "enabled"
        |             }
        |            }
        |          }
        |        }
        |      }
        |    }
        |  }
        |}""".stripMargin('|'))
    Thread.sleep(2000)
    runEngine.run(
      """bulk index "test-index2" "test-parser-type" [
        |{"title": "programming in java"},
        |{"title": "programming in scala"},
        |{"title": "programming in c++"},
        |{"title": "programming in c"},
        |{"title": "programming in camel"}
        |] """.stripMargin)
    Thread.sleep(2000)
    val result = runEngine.run( """aggs in "test-index2" / "test-parser-type" term "title.tags"""")
    assert(result.contains(""""key":"java""""))
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
    val result = runEngine.run( """aggs in "alias-index" "test-parser-type"  avg "age"""")
    assert(result.contains("23"))
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
    assert(result.contains("timestamp"))
  }

  "ELKParser" should "node stats" in {
    val result = runEngine.run("node stats")
    assert(result.contains("nodes"))
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

  "ELKParser" should "multi search" in {
    runEngine.run(
      """bulk index "test-index-name" "test-index-type" [
        |{"name": "test","title":"foo bar","_tip_id": "1"},
        |{"name": "foo","title": "hello world","_tip_id": "2"},
        |{"name": "bar","title": "test po","_tip_id": "1"},
        |{"name": "jack","title": "mnb", "_tip_id": "2"}
        |] """.stripMargin)
    Thread.sleep(2000)
    val result = runEngine.run("""search in "test-index-name" / "test-index-type" match {"title": "foo"} """)
    println(result)
  }
  "ELKParser" should "join search" in {
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
    Thread.sleep(2000)
    val result = runEngine.run(
      """
        |search in "test-index-name" / "test-index-type" join "test-index-name-1" / "test-index-type-1" by "_tip_id"
      """.stripMargin)
    Thread.sleep(1000)
    assert(result.contains("test-index-type"))
  }

  after {
    runEngine.run( """ delete index "*"""")
  }
}

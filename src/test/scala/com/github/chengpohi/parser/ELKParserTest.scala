package com.github.chengpohi.parser

import java.io.ByteArrayOutputStream
import java.net.URI
import java.nio.file.{Files, Paths}

import com.github.chengpohi.helper.ELKCommandTestRegistry
import com.github.chengpohi.repl.ELKInterpreter
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.io.Source

/**
  * elasticservice
  * Created by chengpohi on 1/19/16.
  */
class ELKParserTest
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with ELKCommandTestRegistry {
  val outContent = new ByteArrayOutputStream()
  val errContent = new ByteArrayOutputStream()
  val runEngine: ELKInterpreter = new ELKInterpreter()

  import elasticdsl._

  before {
    runEngine.run("""create index ".elasticdsl"""")
    runEngine.run(""" create index "test-parser-name" """)
    outContent.reset()
    DSL {
      refresh index "*"
    }.await
  }

  "ELKParser" should "get health of elasticsearch" in {
    val result = runEngine.run("health")
    assert(result.contains("cluster_name"))
  }

  "ELKParser" should "index doc by indexName, indexType, fields" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" }""")

    DSL {
      refresh index "*"
    }.await
    //then
    val result = runEngine.run("""search in "test-parser-name"""")
    assert(result.contains(""""name" : "hello""""))
  }

  "ELKParser" should "index doc by indexName, indexType, id and fields" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    DSL {
      refresh index "*"
    }.await
    //then
    val result = runEngine.run(""" search in "test-parser-name" """)
    assert(result.contains(""""_id" : "123""""))
  }

  "ELKParser" should "update doc by id and fields" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    DSL {
      refresh index "*"
    }.await

    runEngine.run(
      """update on "test-parser-name" / "test-parser-type" fields { "name" : "chengpohi"} id "123"""")

    DSL {
      refresh index "*"
    }.await
    //then
    val result = runEngine.run(""" search in "test-parser-name" """)
    assert(result.contains(""""name" : "chengpohi""""))
  }

  "ELKParser" should "reindex by sourceIndex targetIndex sourceType fields" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} """)
    DSL {
      refresh index "*"
    }.await

    runEngine.run(
      """reindex into "test-parser-name" / "test-parser-name-reindex" from "test-parser-type" fields ["name"]""")

    DSL {
      refresh index "*"
    }.await

    val result = runEngine.run(""" search in "test-parser-name-reindex" """)
    //then
    assert(result.contains(""""name" : "hello"""".stripMargin.trim))
  }
  "ELKParser" should "delete doc by id" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields { "name" : "hello", "ppp":"fff" } id "123"""")

    DSL {
      refresh index "*"
    }.await
    //then
    runEngine.run(
      """delete from "test-parser-name" / "test-parser-type" id "123" """)
    DSL {
      refresh index "*"
    }.await
    val result =
      runEngine.run("""search in "test-parser-name" / "test-parser-type"""")
    assert(result.contains(""""hits" : [ ]"""))
  }

  "ELKParser" should "update doc by indexName indexType tuple" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} """)
    DSL {
      refresh index "*"
    }.await
    runEngine.run(
      """update on "test-parser-name" / "test-parser-type" fields {"name":"elasticservice"} """)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(""" search in "test-parser-name" """)
    assert(result.contains(""""name"""".stripMargin.trim))
  }

  "ELKParser" should "analysis doc by specific analyzer" in {
    val result = runEngine.run("""analysis "foo,bar" by "standard"""")
    assert(
      result.contains(
        """"token" : "foo","""
      ))
  }

  "ELKParser" should "index and get doc by id" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"}  id "HJJJJJJH"""")
    val result = runEngine.run(
      """get from "test-parser-name" / "test-parser-type" id "hJJJJJJH"""")
    assert(result.contains(""""_id" : "hJJJJJJH""""))
  }

  "ELKParser" should "extract json data" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields {"name":"hello"} id "HJJJJJJH" """)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """search in "test-parser-name" / "test-parser-type" \\ "hits.hits._source.name"""")
    assert(result === """["hello"]""")
  }

  "ELKParser" should "search data by json" in {
    runEngine.run(
      """index into "test-parser-name" / "test-parser-type" fields {"name":"Hello world", "text": "foo bar"} id "HJJJJJJH" """)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """term query "test-parser-name" "test-parser-type" {"name":"hello", "text": "foo"}""")
    assert(result.contains("Hello world"))
    assert(result.contains("foo bar"))
  }

  "ELKParser" should "set mapping for indexname indextype" in {
    val r = runEngine.run("""mapping "test-mapping" {
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
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(""""test-mapping" mapping""")
    assert(result.contains(""""type":"date""""))
    assert(result.contains(""""keyword"""))
  }

  "ELKParser" should "update mapping" in {
    runEngine.run("""update mapping "test-parser-name" "bookmark" {
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
    DSL {
      refresh index "*"
    }.await
    //val result = runEngine.run(""""test-parser-name" mapping \\ "test-parser-name.mappings.bookmark.properties.name.type" """)
    val result = runEngine.run(""""test-parser-name" mapping""")
    result.contains("keyword") should be(true)
  }

  "ELKParser" should "bulk index docs" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 23}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(""" count "test-parser-name" """)
    assert(result.contains("7"))
    assert(result.contains("false"))
  }

  "ELKParser" should "index newline" in {
    runEngine.run(
      """bulk index "test-parser-name" "test-parser-type" [{"price": 10000, "color": "red"}]""".stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(""" count "test-parser-name" """)
    assert(result.contains(""))
  }
  "ELKParser" should "aggs avg" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 24},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 25},
        |{"name": "hello","age": 25},
        |{"name": "hello","age": 22},
        |{"name": "hello","age": 22}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """aggs in "test-parser-name" / "test-parser-type" avg "age"""")
    assert(result.contains("""23"""))
  }

  "ELKParser" should "hist aggs" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
        |{"name": "hello1","created_at": 1478844495882},
        |{"name": "hello2","created_at": 1478844595882},
        |{"name": "hello3","created_at": 1478844695882},
        |{"name": "hello4","created_at": 1478844795882},
        |{"name": "hello5","created_at": 1478844895882},
        |{"name": "hello6","created_at": 1478844995882},
        |{"name": "hello7","created_at": 1598846395882}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """aggs in "test-parser-name" / "test-parser-type" hist "test" interval "day" field "created_at"""")
    assert(result.contains("""aggregations"""))
  }
  "ELKParser" should "dump index" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
        |{"name": "hello1","created_at": 1478844495882},
        |{"name": "hello2","created_at": 1478844595882},
        |{"name": "hello3","created_at": 1478844695882},
        |{"name": "hello4","created_at": 1478844795882},
        |{"name": "hello5","created_at": 1478844895882},
        |{"name": "hello6","created_at": 1478844995882},
        |{"name": "hello7","created_at": 1598846395882}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val uri = runEngine.run("""dump index "test-parser-name" > dump.txt""")
    val u: URI = new URI(uri)
    Source
      .fromFile(Paths.get(u).toFile)
      .getLines()
      .foreach(s => {
        runEngine.run(s)
      })
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run("""count "test-parser-name"""")
    result should contain inOrder ('1', '4')
    Files.delete(Paths.get(u))
  }

  "ELKParser" should "aggs terms" in {
    val p = runEngine.run("""mapping "test-index2" {
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
    DSL {
      refresh index "*"
    }.await
    runEngine.run("""bulk index "test-index2" "test-parser-type" [
        |{"title": "programming in java"},
        |{"title": "programming in scala"},
        |{"title": "programming in c++"},
        |{"title": "programming in c"},
        |{"title": "programming in camel"}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """aggs in "test-index2" / "test-parser-type" term "title.tags"""")
    assert(result.contains(""""key" : "java""""))
  }

  "ELKParser" should "alias index" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 24},
        |{"name": "hello","age": 23},
        |{"name": "hello","age": 25},
        |{"name": "hello","age": 25},
        |{"name": "hello","age": 22},
        |{"name": "hello","age": 22}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    runEngine.run("""alias "alias-index" "test-parser-name"""")
    DSL {
      refresh index "*"
    }.await
    val result =
      runEngine.run("""aggs in "alias-index" "test-parser-type"  avg "age"""")
    assert(result.contains("23"))
  }

  "ELKParser" should "create snapshot" in {
    runEngine.run(
      """create repository "test_snapshot" "fs" {"compress": "true", "location": "./target/elkrepo"} """.stripMargin)
    DSL {
      refresh index "*"
    }.await

    runEngine.run("""create snapshot "snapshot1" "test_snapshot"""".stripMargin)
    DSL {
      refresh index "*"
    }.await

    val result =
      runEngine.run("""get snapshot "snapshot1" "test_snapshot"""".stripMargin)

    DSL {
      refresh index "*"
    }.await

    runEngine.run("""close index "*"""".stripMargin)

    DSL {
      refresh index "*"
    }.await

    runEngine.run(
      """restore snapshot "snapshot1" "test_snapshot"""".stripMargin)

    DSL {
      refresh index "*"
    }.await

    runEngine.run("""close index "*"""".stripMargin)

    DSL {
      refresh index "*"
    }.await

    runEngine.run("""open index "*"""".stripMargin)

    DSL {
      refresh index "*"
    }.await

    runEngine.run("""delete snapshot "snapshot1" "test_snapshot"""".stripMargin)

    assert(result.contains("\"snapshot\" : \"snapshot1\","))
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
    val result = runEngine.run(""""test-parser-name" settings""")
    assert(result.contains("test-parser-name"))
  }

  "ELKParser" should "retrieve pending tasks" in {
    val result = runEngine.run("""pending tasks""")
    assert(result.contains(""""tasks""""))
  }

  "ELKParser" should "list all indices" in {
    val result = runEngine.run("""cluster state""")
    assert(result.contains("indices"))
  }

  "ELKParser" should "wait for status" in {
    runEngine.run("""bulk index "test-parser-name" "test-parser-type" [
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

  /*"ELKParser" should "list help for command" in {
    val result = runEngine.run("create index ?")
    assert(result.contains(""""description":"create index by index name""""))
  }*/

  "ELKParser" should "create analyzer" in {
    runEngine.run(
      """create analyzer {"analyzer":{"myAnalyzer":{"type":"pattern","pattern":"\s+"}}}""")
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run("""".elasticdsl" settings""")
    assert(result.contains("myAnalyzer"))
  }

  "ELKParser" should "multi search" in {
    runEngine.run("""bulk index "test-index-name" "test-index-type" [
        |{"name": "test","title":"foo bar","_tip_id": "1"},
        |{"name": "foo","title": "hello world","_tip_id": "2"},
        |{"name": "bar","title": "test po","_tip_id": "1"},
        |{"name": "jack","title": "mnb", "_tip_id": "2"}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """search in "test-index-name" / "test-index-type" match {"title": "foo"} """)
    result.contains(""""name" : "test"""") should be(true)
  }
  "ELKParser" should "join search" in {
    runEngine.run("""bulk index "test-index-name-1" "test-index-type-1" [
        |{"tip": "hello","_tip_id": "1"},
        |{"tip": "world","_tip_id": "2"}
        |] """.stripMargin)
    runEngine.run("""bulk index "test-index-name" "test-index-type" [
        |{"name": "test","_tip_id": "1"},
        |{"name": "foo","_tip_id": "2"},
        |{"name": "bar","_tip_id": "1"},
        |{"name": "bar1","_tip_id": "1"},
        |{"name": "bar2","_tip_id": "1"},
        |{"name": "bar3","_tip_id": "1"},
        |{"name": "bar4","_tip_id": "1"},
        |{"name": "bar5","_tip_id": "1"},
        |{"name": "bar6","_tip_id": "1"},
        |{"name": "bar7","_tip_id": "1"},
        |{"name": "bar8","_tip_id": "1"},
        |{"name": "bar9","_tip_id": "1"},
        |{"name": "bar10","_tip_id": "1"},
        |{"name": "jack","_tip_id": "2"}
        |] """.stripMargin)
    DSL {
      refresh index "*"
    }.await
    val result = runEngine.run(
      """
        |search in "test-index-name-1" / "test-index-type-1" join "test-index-name" / "test-index-type" by "_tip_id"
      """.stripMargin)
    assert(result.contains("test-index-type"))
  }

  after {
    runEngine.run(""" delete index "*"""")
  }
}

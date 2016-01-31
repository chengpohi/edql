package com.github.chengpohi.parser

import java.io.ByteArrayOutputStream

import com.github.chengpohi.ELKRunEngine
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
 * elasticservice
 * Created by chengpohi on 1/19/16.
 */
class ELKParserTest extends FlatSpec with BeforeAndAfter {
  val outContent = new ByteArrayOutputStream()
  val errContent = new ByteArrayOutputStream()

  before {
    ELKRunEngine.run( """ createIndex "test-parser-name" """)
    outContent.reset()
  }

  "ELKParser" should "get health of elasticsearch" in {
    Console.withOut(outContent) {
      ELKRunEngine.run("health")
    }
    assert(outContent.toString.contains("cluster_name"))
  }

  "ELKParser" should "index doc by indexName, indexType, fields" in {
    ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)"""")

    Thread.sleep(1000)
    //then
    ELKRunEngine.run( """ count "test-parser-name" """)
  }

  "ELKParser" should "reindex by sourceIndex targetIndex sourceType fields" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)" """)
      Thread.sleep(1000)
      ELKRunEngine.run( """ reindex "test-parser-name" "test-parser-name-reindex" "test-parser-type" "name" """)

      Thread.sleep(2000)

      ELKRunEngine.run( """ query "test-parser-name-reindex" """)
      ELKRunEngine.run( """ delete "test-parser-name-reindex" """)
      //then
    }
    assert(outContent.toString.contains(
      """
        |"_source":{"name":"hello"}
      """.stripMargin.trim))
  }

  "ELKParser" should "update doc by indexName indexType tuple" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)" """)
      Thread.sleep(1000)
      ELKRunEngine.run( """ update "test-parser-name" "test-parser-type" "(name, elasticservice)" """)
      Thread.sleep(3000)
      ELKRunEngine.run( """ query "test-parser-name" """)
    }
    assert(outContent.toString.contains(
      """
        |"_source":{"name":"elasticservice"}
      """.stripMargin.trim))
  }

  "ELKParser" should "analysis doc by specific analyzer" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """analysis "standard" "foo,bar"""")
    }
    assert(outContent.toString.contains(
      """"token":"foo","start_offset":0,""".stripMargin
    ))
  }

  "ELKParser" should "delete index type by index type" in {
    ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)" """)
    ELKRunEngine.run( """delete "test-parser-name" "test-parser-type" """)
    Console.withOut(outContent) {
      ELKRunEngine.run( """"test-parser-name" mapping""")
    }
    assert(!outContent.toString.contains("test-parser-type"))
  }

  "ELKParser" should "index and get doc by id" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)" "HJJJJJJH" """)
      ELKRunEngine.run( """get "test-parser-name" "test-parser-type" "hJJJJJJH"""")
    }
    assert(outContent.toString.contains( """"_id":"hJJJJJJH""""))
  }

  "ELKParser" should "extract json data" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """index "test-parser-name" "test-parser-type" "(name, hello)" "HJJJJJJH" """)
      Thread.sleep(1000)
      ELKRunEngine.run( """query "test-parser-name" \\ "name" """)
    }
    assert(outContent.toString.contains( """[{"name":"hello"}]"""))
  }

  "ELKParser" should "set mapping for indexname indextype" in {
    Console.withOut(outContent) {
      ELKRunEngine.run( """mapping "test-mapping" "bookmark" "[(name,string),(created_at,date)]" """)
      Thread.sleep(1000)
      ELKRunEngine.run( """ "test-mapping" mapping """)
    }
    assert(outContent.toString.contains(""""date":{"type":"date","format":"dateOptionalTime"}"""))
    ELKRunEngine.run( """ delete "test-mapping"""")
  }
  after {
    ELKRunEngine.run( """ delete "test-parser-name"""")
  }
}

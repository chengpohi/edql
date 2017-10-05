package com.github.chengpohi.api.dsl

import java.util.Date

import com.github.chengpohi.annotation.Analyzer
import com.github.chengpohi.helper.ELKTestTrait

/**
  * elasticdsl
  * Created by chengpohi on 10/04/17.
  */
class DSLMappingTest extends ELKTestTrait {

  import elasticdsl._

  case class Bookmark(@Analyzer("tag_analyzer") comment: String,
                      created: Date,
                      name: String,
                      nameTags: String,
                      tabId: String)

  case object UserIndexSettings extends IndexSettings {
    override val analyzer = Analyzer(
      name = "tag_analyzer",
      tpe = "standard",
      tokenizer = "standard",
      filter = "standard",
      stopwordsPath = this.getClass.getResource("/words.txt").getFile
    )
    override val filter =
      Filter(name = "tags_filter",
             tpe = "keep",
             keepwordsPath =
               this.getClass.getResource("/completions.txt").getFile)
  }

  it should "parse response to json" in {
    val indexName = "user1"
    val res = DSL {
      create index indexName mappings Mappings[Bookmark] settings UserIndexSettings
    }.toJson

    println(res)
    val res2 = DSL {
      get mapping indexName
    }.toJson
    res2 should include("tag_analyzer")

    val res3 = DSL {
      get settings indexName
    }.toJson

    res3 should include("tag_analyzer")
    res3 should include("tags_filter")
  }
}

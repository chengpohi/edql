package com.github.chengpohi.api.dsl

import java.util.Date

import com.github.chengpohi.annotation.{Alias, Analyzer, CopyTo, Index}
import com.github.chengpohi.helper.ELKTestTrait

/**
  * elasticdsl
  * Created by chengpohi on 10/04/17.
  */
class EntityMappingTest extends ELKTestTrait {

  import elasticdsl._

  case class Bookmark(@Analyzer("tag_analyzer") comment: String,
                      created: Date,
                      @CopyTo("name_tags") name: String,
                      @Alias("name_tags") nameTags: String,
                      @Index("not_analyzed") url: String,
                      @Index("not_analyzed") tabId: String)

  case class Tab(createdAt: Date,
                 name: String,
                 @Index("not_analyzed") tabId: String)

  case class Info(name: String,
                  @Index("not_analyzed") email: String,
                  @Index("not_analyzed") password: String,
                  createdAt: Date)

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
    DSL {
      create index indexName mappings Mappings[Bookmark, Info, Tab] settings UserIndexSettings
    }.toJson

    val res2 = DSL {
      get mapping indexName
    }.toJson
    res2 should equalToJSONFile("mapping.json")

    val res3 = DSL {
      get settings indexName
    }.toJson

    res3 should equalToJSONFile("settings.json")
  }
}

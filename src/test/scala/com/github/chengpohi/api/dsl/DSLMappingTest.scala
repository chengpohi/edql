package com.github.chengpohi.api.dsl

import java.util.Date

import com.github.chengpohi.annotation.Analyzer
import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.json4s.DefaultFormats
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

/**
  * elasticdsl
  * Created by chengpohi on 10/04/17.
  */
class DSLMappingTest
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with ELKCommandTestRegistry {

  import elasticdsl._

  before {
    DSL {
      create index "testindex"
    }.await
    DSL {
      refresh index "testindex"
    }.await
  }

  case class Bookmark(@Analyzer("myanalyzer") comment: String,
                      created: Date,
                      name: String,
                      nameTags: String,
                      tabId: String)

  val a1 = create analyzer "tag_analyzer" tpe "standard" tokenizer "standard" filter "standard, tags_filter" stopwords "stop_words.txt"
  val fi = create filter "tags_filter" tpe "keep" keepwords "tags.csv"

  case object MySettings extends IndexSettings {
    override val analyzer = Analyzer(
      name = "tag_analyzer",
      tpe = "standard",
      tokenizer = "standard",
      filter = "standard",
      stopwordsPath = "./stop_words.txt"
    )
    override val filter =
      Filter(name = "tags_filter", tpe = "keep", keepwordsPath = "tags.csv")
  }

  it should "parse response to json" in {
    import org.json4s.native.Serialization.write
    implicit val formats = DefaultFormats

    val mappings = Mappings[Bookmark]

    println(write(mappings.build))
    //create index "user" mappings Mappings(Bookmark.getClass) settings MySettings
  }

  after {
    DSL {
      delete index ALL
    }
  }
}

package com.github.chengpohi.dsl.eql.usage

import com.github.chengpohi.helper.EQLTestTrait

import scala.io.Source

object NgramUsage extends EQLTestTrait {

  def main(args: Array[String]): Unit = {
    import eql._

    val corpus = Source
      .fromInputStream(
        this.getClass.getResourceAsStream("/training/corpus.txt"))
      .getLines()
      .zipWithIndex
      .map(s => Map("text" -> s._1, "id" -> s._2))
      .toList

    val _indexType = "foo"
    val _indexName = "bar"
    EQL {
      create index _indexName tokenizer List(
        create tokenizer "my_tokenizer" tpe "ngram" min_gram 3 max_gram 3 token_chars List(
          "letter",
          "digit")
      ) analyzers List(
        create analyze "my_analyzer" tokenizer "my_tokenizer"
      ) fields List(
        create field "text" in _indexType tpe "text" store true analyzer "my_analyzer"
      ) settings (
        IndexSettingsDefinition() number_of_replicas 0 number_of_shards 1
      )
    }.await

    EQL {
      index into _indexName / _indexType doc corpus
    }.await

    val response = EQL {
      analyze text "Quick Fox" tokenizer "ngram"
    }.await.json

    println(response)

  }

}

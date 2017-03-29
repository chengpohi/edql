package com.github.chengpohi.api.dsl.usage

import com.github.chengpohi.helper.ELKCommandTestRegistry
import org.elasticsearch.common.lucene.uid.Versions

import scala.io.Source

object TermsBayesian extends App {
  val dsl = ELKCommandTestRegistry.elasticdsl
  val corpus = Source
    .fromInputStream(this.getClass.getResourceAsStream("/training/corpus.txt"))
    .getLines()
    .zipWithIndex
    .map(s => Map("text" -> s._1, "id" -> s._2)).toList

  import dsl._

  val testStr = "I have never seen a better programming language"

  val _indexType = "tweet"
  val _indexName = "test"
  DSL {
    create index _indexName analyzers List(
      create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List("lowercase", "type_as_payload")
    ) fields List(
      create field "text" in _indexType tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer"
    ) settings (IndexSettingsDefinition() number_of_replicas 0 number_of_shards 1)
  }.await

  val mapping = DSL {
    get mapping "test"
  }.toJson

  println(mapping)

  DSL {
    index into _indexName / _indexType doc corpus
  }.await

  Thread.sleep(2000)

  val response = DSL {
    search in _indexName / _indexType size corpus.size
  }.await.toJson

  println(response)

  val r: String = client.prepareTermVectors(_indexName, _indexType, "1")
    .setSelectedFields("text")
    .setOffsets(true)
    .setPayloads(true)
    .setTermStatistics(true)
    .setFieldStatistics(true)
    .execute().toJson
  println(r)
}

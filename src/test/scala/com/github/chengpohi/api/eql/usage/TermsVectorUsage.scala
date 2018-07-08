package com.github.chengpohi.api.eql.usage

import com.github.chengpohi.helper.EQLTestTrait
import org.elasticsearch.action.termvectors.TermVectorsResponse

import scala.concurrent.Future
import scala.io.Source

object TermsVectorUsage extends EQLTestTrait {
  def main(args: Array[String]): Unit = {
    val corpus = Source
      .fromInputStream(
        this.getClass.getResourceAsStream("/training/corpus.txt"))
      .getLines()
      .zipWithIndex
      .map(s => Map("text" -> s._1, "id" -> s._2))
      .toList

    import eql._

    val testStr = "I have never seen a better programming language"

    val _indexType = "foo"
    val _indexName = "bar"
    EQL {
      create index _indexName analyzers List(
        create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List(
          "lowercase",
          "type_as_payload")
      ) fields List(
        create field "text" in _indexType tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer"
      ) settings (IndexSettingsDefinition() number_of_replicas 0 number_of_shards 1)
    }.await

    val mapping = EQL {
      get mapping _indexName
    }.toJson

    println(mapping)

    EQL {
      index into _indexName / _indexType doc corpus
    }.await

    Thread.sleep(2000)

    val response = EQL {
      search in _indexName / _indexType size corpus.size
    }.await.json

    println(response)

    val r: Future[TermVectorsResponse] = client
      .prepareTermVectors(_indexName, _indexType, "1")
      .setSelectedFields("text")
      .setOffsets(true)
      .setPayloads(true)
      .setTermStatistics(true)
      .setFieldStatistics(true)
      .execute()
    println(r.await.json)
  }
}

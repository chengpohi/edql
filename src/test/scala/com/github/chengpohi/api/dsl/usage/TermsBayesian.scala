package com.github.chengpohi.api.dsl.usage

import com.github.chengpohi.helper.ELKCommandTestRegistry

import scala.io.Source

object TermsBayesian extends App {
  val dsl = ELKCommandTestRegistry.elasticdsl
  val corpus = Source
    .fromInputStream(this.getClass.getResourceAsStream("/training/corpus.txt"))
    .getLines()
    .map(s => Map("text" -> s)).toList

  import dsl._

  val testStr = "I have never seen a better programming language"

  DSL {
    create index "test" analyzers List(
      create analyze "fulltext_analyzer" tpe "custom" tokenizer "whitespace" filter List("lowercase", "type_as_payload")
    ) fields List(
      create field "text" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store true analyzer "fulltext_analyzer",
      create field "fullname" in "tweet" tpe "text" term_vector "with_positions_offsets_payloads" store false analyzer "fulltext_analyzer"
    )
  }.await

  DSL {
    index into "test" / "tweet" doc corpus
  }.await

  Thread.sleep(2000)

  val response = DSL {
    search in "test" / "tweet" size corpus.size
  }.await.toJson
  println(response)
}

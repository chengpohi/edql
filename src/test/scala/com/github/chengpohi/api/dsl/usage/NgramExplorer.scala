package com.github.chengpohi.api.dsl.usage

import scala.io.Source

object NgramExplorer extends App {
  val corpus = Corpus(Source
    .fromInputStream(this.getClass.getResourceAsStream("/training/corpus.txt"))
    .getLines()
    .map(_.split("\\s+").toList) // simple tokenizer
    .flatMap(l => l.sliding(2).map(r => (r(0), r(1))).toList) //2-gram
    .toStream)

  val test1 = ("I", "have")
  val test2 = ("have", "I")
  val terms1 = corpus.terms(test1)
  val terms2 = corpus.terms(test2)
  println("total size: " + corpus.size)
  println(s"${test1} size: " + terms1._1)
  println(s"${test2} size: " + terms2._1)

  println(s"P(${test1}) = " + terms1._2)
  println(s"P(${test2}) = " + terms2._2)

  //I have never seen a better programming language


  val sentence = "have programming a seen never I language better"
  println(corpus.reorderSentence(sentence))
}

case class Corpus(grams: Stream[(String, String)]) {
  def terms(s: (String, String)): (Int, Double) = {
    val count = grams.count(c => c == s)
    (count, count.toDouble / grams.size)
  }

  def size: Int = grams.size

  def reorderSentence(sentence: String): String = {
    val tokenizer = sentence.split("\\s+")

    val combinations = tokenizer.combinations(2).flatMap(i =>
      List((i(0), i(1)), (i(1), i(0)))
    ).toList

    val r = combinations.map(i =>
      (i, terms(i))
    ).filter(_._2._1 > 0).map(_._1)
    concatSentence(r, tokenizer.size)
  }


  private def concatSentence(r: List[(String, String)], n: Int): String = {
    val head = r.find(i => !r.exists(_._2 == i._1))
    head match {
      case Some(t) => {
        (1 until n).foldLeft(List(t._1))((a, b) => {
          a :+ r.find(i => i._1 == a.last).map(_._2).getOrElse("")
        }).mkString(" ")
      }
      case _ => "Concat error, There is a circle in this sentence"
    }
  }
}

package com.github.chengpohi.nlp

import breeze.linalg.min

/**
  * elasticshell
  * Created by chengpohi on 8/24/16.
  */
object NLP {
  implicit class Str(a: String) {
    def distance(b: String): Int = a.foldLeft((0 to b.length).toList)((prev, x) =>
      (prev zip prev.tail zip b).scanLeft(prev.head + 1) {
        case (h, ((d, v), y)) => min(min(h + 1, v + 1), d + (if (x == y) 0 else 1))
      }) last
  }

  implicit class Words[K <: String](it: Seq[K]) {
    def unigram = ngram(1)
    def bigram = ngram(2)
    def ngram(n: Int): Stream[Seq[K]] = it.sliding(n).toStream
  }
}

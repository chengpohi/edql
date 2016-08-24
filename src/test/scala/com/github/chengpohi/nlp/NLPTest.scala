package com.github.chengpohi.nlp


import org.scalatest.{FlatSpec, ShouldMatchers}

/**
  * elasticshell
  * Created by chengpohi on 8/24/16.
  */
class NLPTest extends FlatSpec with ShouldMatchers{
  import NLP._
  it should "calculate strings distance" in {
    val distance1: Int = "Hello" distance "Heloo"
    assert(distance1 === 1)
    val distance2: Int = "Heplo" distance "Heloo"
    assert(distance2 === 2)
    val distance3: Int = "" distance "Heloo"
    assert(distance3 === 5)
    val distance4: Int = "aasdksjdkfjaksdjfkjiasdfsdfasdfasdfasdf" distance "wqeruqwerkkjklasdiasdfasdfmnzxcvj"
    assert(distance4 === 27)
  }

  it should "calculate strings ngram" in {
    val str: List[String] = List("hello", "world", "foo", "bar")
    val result = str.unigram.toList
    assert(result ===  List(List("hello"), List("world"), List("foo"), List("bar")))
    val result2 = str.bigram.toList
    assert(result2 === List(List("hello", "world"), List("world", "foo"), List("foo", "bar")))
  }
}

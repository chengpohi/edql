package com.github.chengpohi.edql.parser

import com.intellij.psi.impl.PsiFileFactoryImpl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io._

class EDQLPsiInterceptorTest extends AnyFlatSpec with Matchers with InterceptFunction {
  private val definition = new EDQLParserDefinition
  private val edql = "edql"
  private val fileFactoryImpl: PsiFileFactoryImpl = MockPsiFactoryBuilder.apply("edql", definition)
  val factory: EDQLParserFactory = EDQLParserFactory.apply(edql, definition, fileFactoryImpl)
  val parser = new EDQLPsiInterceptor(factory)

  it should "parse edql" in {
    val text = Source.fromResource("test.edql").getLines().mkString(System.lineSeparator())

    val res = parser.parse(text).get

    res.foreach(i => println(i.toString))
    res.size should be(23)
  }


  it should "parse json" in {
    val text = Source.fromResource("test.json").getLines().mkString(System.lineSeparator())

    val res = parser.parseJson(text)
    res.isSuccess should be(true)
    println(res)
  }


  it should "parse illegal json" in {
    val text = Source.fromResource("illegal.json").getLines().mkString(System.lineSeparator())

    val res = parser.parseJson(text)
    println(res)
    res.isFailure should be(true)
  }


}

package com.github.chengpohi.parser

import fastparse.core.Parsed.{Failure, Success}

import scala.collection.mutable.ArrayBuffer


/**
 * scala-parser-combinator
 * Created by chengpohi on 12/30/15.
 */
object ELKParser extends ELKInstrumentParser{
  import fastparse.all._

  val methodParameter = P(space ~ "var" ~ space ~ variableChars.rep.! ~ ",".?).map(s => "$" + s)
  val functionDefine: P[(String, Map[String, String])] =
    P(space ~ "function" ~ space ~/ variableChars.rep.! ~ "(" ~ methodParameter.rep ~ ")" ~ space ~ "{" ~ space).map(f =>
      (f._1, f._2.map(i => i -> "").toMap))
  val function = P(functionDefine ~/ instrument.rep ~ "}" ~ space).map(f => ELK.Function((f._1, f._3, f._2)))

  val elkParser: P[Seq[ELK.AST]] = P(space ~ (instrument | function).rep ~ End)

  def generateAST(parsed: Parsed[Seq[ELK.AST]]): (Map[String, (Seq[ELK.Instrument], Map[String, String])], ArrayBuffer[ELK.Instrument]) = {
    var functions = Map[String, (Seq[ELK.Instrument], Map[String, String])]()
    var instruments = ArrayBuffer[ELK.Instrument]()
    parsed match {
      case Success(f, state) =>
        f.foreach {
          case i: ELK.Function =>
            val (name, inss, params) = i.value
            functions += name ->(inss, params)
          case i: ELK.Instrument =>
            instruments += i
        }
      case f: Failure =>
        println(f)
    }
    (functions, instruments)
  }
}

object ELK {
  sealed trait AST extends Any {
    def value: Any
  }

  case class Instrument(value: (String, Option[(Seq[String]) => String], Seq[String])) extends AnyVal with AST

  case class Function(value: (String, Seq[Instrument], Map[String, String])) extends AnyVal with AST
}


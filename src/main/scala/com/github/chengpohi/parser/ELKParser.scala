package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.{Str, Val}
import com.github.chengpohi.parser.ELK.Instrument
import fastparse.core.Parsed.{Failure, Success}


/**
  * scala-parser-combinator
  * Created by chengpohi on 12/30/15.
  */
class ELKParser extends ELKInstrumentParser {
  import fastparse.all._
  val parserUtils = new ParserUtils
  val methodParameter = P(space ~ "var" ~ space ~ variableChars.rep.! ~ ",".?).map(s => "$" + s)
  P(space ~ "function" ~ space ~/ variableChars.rep.! ~ "(" ~ methodParameter.rep ~ ")" ~ space ~ "{" ~ space).map(f =>
    (f._1, f._2.map(i => i -> "").toMap))

  val elkParser: P[Seq[ELK.AST]] = P(space ~ instrument.rep ~ End)

  def generateAST(parsed: Parsed[Seq[ELK.AST]]): (Map[String, (Seq[ELK.Instrument], Map[String, String])], Seq[ELK.Instrument]) = {
    var functions = Map[String, (Seq[ELK.Instrument], Map[String, String])]()
    val instruments = parsed match {
      case Success(f, state) => f map {
        case i: ELK.Instrument => i
      }
      case f: Failure => Seq(Instrument(("error", Some(parserUtils.error), Seq(Str(f.msg), Str(f.extra.input)))))
    }
    (functions, instruments)
  }
}

object ELK {
  sealed trait AST extends Any {
    def value: Any
  }
  case class Instrument(value: (String, Option[Seq[Val] => String], Seq[Val])) extends AnyVal with AST
}


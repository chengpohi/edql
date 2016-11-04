package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.{Str, Val}
import com.github.chengpohi.parser.ELK.Instrument
import fastparse.core.Parsed.{Failure, Success}
import fastparse.noApi._

import scala.concurrent.Future


/**
  * scala-parser-combinator
  * Created by chengpohi on 12/30/15.
  */
class ELKParser(elkCommand: ELKCommand, parserUtils: ParserUtils)
  extends ELKInstructionParser(elkCommand, parserUtils) {

  import WhitespaceApi._

  val methodParameter = P("var" ~ variableChars.rep.! ~ ",".?).map(s => "$" + s)
  P("function" ~/ variableChars.rep.! ~ "(" ~ methodParameter.rep ~ ")" ~ "{").map(f =>
    (f._1, f._2.map(i => i -> "").toMap))

  val elkParser: P[Seq[ELK.AST]] = P(WL0 ~ instrument.rep ~ End)

  def generateAST(parsed: Parsed[Seq[ELK.AST]]): (Map[String, (Seq[ELK.Instrument], Map[String, String])], Seq[ELK.Instrument]) = {
    var functions = Map[String, (Seq[ELK.Instrument], Map[String, String])]()
    val instruments = parsed match {
      case Success(f, state) => f map {
        case i: ELK.Instrument => i
      }
      case Failure(_, _, t) => Seq(Instrument(("error", Some(parserUtils.error), Seq(Str(t.traced.trace), Str(t.traced.trace)))))
    }
    (functions, instruments)
  }
}

object ELK {

  sealed trait AST extends Any {
    def value: Any
  }

  case class Instrument(value: (String, Option[Seq[Val] => Future[String]], Seq[Val])) extends AnyVal with AST

}


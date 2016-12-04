package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.Str
import fastparse.core.Parsed.{Failure, Success}
import fastparse.noApi._


/**
  * scala-parser-combinator
  * Created by chengpohi on 12/30/15.
  */
class ELKParser(elkCommand: ELKCommand, parserUtils: ParserUtils)
  extends ELKInstructionParser(elkCommand, parserUtils) {

  import WhitespaceApi._

  val elkParser: P[Seq[INSTRUMENT_TYPE]] = P(WL0 ~ instrument.rep ~ End)

  def generateAST(parsed: Parsed[Seq[INSTRUMENT_TYPE]]): Seq[INSTRUMENT_TYPE] = {
    val instruments = parsed match {
      case Success(f, state) => f map {
        i: INSTRUMENT_TYPE => i
      }
      case Failure(_, _, t) => Seq(("error", Some(parserUtils.error), Seq(Str(t.traced.trace), Str(t.traced.trace))))
    }
    instruments
  }
}


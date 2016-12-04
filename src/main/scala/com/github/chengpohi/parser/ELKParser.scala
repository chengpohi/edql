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

  val elkParser: P[Seq[Instruction]] = P(WL0 ~ instrument.rep ~ End)

  def generateDefinitions(parsed: Parsed[Seq[Instruction]]): Seq[Instruction] = {
    val instructions = parsed match {
      case Success(ins, state) => ins
      case Failure(_, _, t) =>
        Seq(Instruction("error", parserUtils.error, Seq(Str(t.traced.trace), Str(t.traced.trace))))
    }
    instructions
  }
}


package com.github.chengpohi.parser

import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.collection.JsonCollection.Str
import fastparse.core.Parsed.{Failure, Success}
import fastparse.noApi._

/**
  * scala-parser-combinator
  * Created by chengpohi on 12/30/15.
  */
class ELKParser(eql: EQLClient) extends ELKInstructionParser {

  override val interceptFunction: InterceptFunction = new InterceptFunction(eql)

  import WhitespaceApi._

  val instructionParser: P[Seq[interceptFunction.Instruction]] = P(
    WL0 ~ instrument.rep ~ End)

  def generateDefinitions(parsed: Parsed[Seq[interceptFunction.Instruction]])
    : Seq[interceptFunction.Instruction] = {
    val instructions = parsed match {
      case Success(ins, state) => ins
      case Failure(_, _, t) =>
        Seq(
          interceptFunction.Instruction(
            "error",
            interceptFunction.error,
            Seq(Str(t.traced.trace), Str(t.traced.trace))))
    }
    instructions
  }
}

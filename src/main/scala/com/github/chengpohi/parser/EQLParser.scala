package com.github.chengpohi.parser

import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.collection.JsonCollection.Str
import fastparse.core.Parsed.{Failure, Success}
import fastparse.noApi._

class EQLParser(eql: EQLClient) extends EQLInstructionParser {

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
//            Seq(Str(t.traced.trace), Str(t.traced.trace)))
            Seq(Str("command not found")))
        )
    }
    instructions
  }
}

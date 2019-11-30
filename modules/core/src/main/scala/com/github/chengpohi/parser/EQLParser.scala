package com.github.chengpohi.parser

import com.github.chengpohi.context.EQLContext
import fastparse.core.Parsed.{Failure, Success}
import fastparse.noApi._

class EQLParser(e: EQLContext) extends EQLInstructionParser {
  override val eql: EQLContext = e

  import WhitespaceApi._

  type PSI = Parsed[Seq[Instruction2]]

  val instructionParser: P[Seq[Instruction2]] = P(
    WL0 ~ instrument.rep ~ End)

  def generateDefinitions(parsed: PSI): Seq[Instruction2] = {
    parsed match {
      case Success(ins, state) => ins
      case Failure(_, _, t) =>
        Seq(ErrorInstruction("command not found"))
    }
  }

}

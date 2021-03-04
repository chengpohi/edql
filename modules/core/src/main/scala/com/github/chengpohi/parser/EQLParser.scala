package com.github.chengpohi.parser

import com.github.chengpohi.context.EQLContext
import fastparse.Parsed.{Failure, Success}
import fastparse._
import fastparse.NoWhitespace._


class EQLParser(e: EQLContext) extends EQLInstructionParser {
  override val eql: EQLContext = e

  type PSI = Parsed[Seq[Instruction2]]

  def instruction(s: String): PSI = parse(s, instrument(_))

  def generateInstructions(parsed: PSI): Seq[Instruction2] = {
    parsed match {
      case Success(ins, state) => ins
      case Failure(_, _, t) =>
        Seq(ErrorInstruction("command not found"))
    }
  }

}

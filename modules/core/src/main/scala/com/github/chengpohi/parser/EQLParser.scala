package com.github.chengpohi.parser

import fastparse.Parsed.{Failure, Success}
import fastparse._


class EQLParser extends EQLInstructionParser {
  type PSI = Parsed[Seq[Instruction2]]

  def instruction(s: String): PSI = parse(s, instrument(_))

  def gi(parsed: PSI): Seq[Instruction2] = {
    parsed match {
      case Success(ins, state) => ins
      case Failure(_, _, t) =>
        Seq(ErrorInstruction("command not found"))
    }
  }

}

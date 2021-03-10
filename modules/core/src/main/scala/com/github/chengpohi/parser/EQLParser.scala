package com.github.chengpohi.parser

import fastparse.Parsed.{Failure, Success}
import fastparse._

import scala.util.Try


class EQLParser extends EQLInstructionParser {
  type PSI = Parsed[Seq[Instruction2]]

  def instruction(s: String): PSI = parse(s, instrument(_))

  def gi(parsed: PSI): Try[Seq[Instruction2]] = {
    parsed match {
      case Success(ins, state) =>
        scala.util.Success(ins)
      case failure: Failure =>
        scala.util.Failure(new RuntimeException(failure.msg));
    }
  }

}

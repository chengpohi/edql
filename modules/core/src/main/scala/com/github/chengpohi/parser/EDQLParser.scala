package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.Parsed.{Failure, Success}
import fastparse._

import scala.util.Try


class EDQLParser extends EDQLInstructionParser {
  type PSI = Parsed[Seq[Instruction2]]

  def instruction(s: String): PSI = parse(s, instrument(_))

  def gi(parsed: PSI): Try[Seq[Instruction2]] = {
    parsed match {
      case Success(ins, _) =>
        scala.util.Success(ins)
      case failure: Failure =>
        scala.util.Failure(new RuntimeException(failure.msg));
    }
  }


  def parsePSI: String => PSI = (s: String) => instruction(s)

  def generateInstructions(source: String): Try[Seq[Instruction2]] = {
    (parsePSI andThen gi).apply(source)
  }

  def parseJson(source: String): Try[JsonCollection.Val] = {
    val result = parse(source, jsonExpr(_))
    result match {
      case Success(ins, successIndex) =>
        if (successIndex != source.length) {
          return scala.util.Failure(new RuntimeException("illegal json: " + source));
        }
        scala.util.Success(ins)
      case failure: Failure =>
        scala.util.Failure(new RuntimeException(failure.msg));
    }
  }

}

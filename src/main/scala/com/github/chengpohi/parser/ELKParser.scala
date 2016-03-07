package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection.Val
import fastparse.core.Parsed.{Failure, Success}

import scala.collection.mutable.ArrayBuffer


/**
 * scala-parser-combinator
 * Created by chengpohi on 12/30/15.
 */
object ELKParser extends ELKInstrumentParser{
  import fastparse.all._

  val methodParameter = P(space ~ "var" ~ space ~ variableChars.rep.! ~ ",".?).map(s => "$" + s)
    P(space ~ "function" ~ space ~/ variableChars.rep.! ~ "(" ~ methodParameter.rep ~ ")" ~ space ~ "{" ~ space).map(f =>
      (f._1, f._2.map(i => i -> "").toMap))

  val elkParser: P[Seq[ELK.AST]] = P(space ~ instrument.rep ~ End)

  def generateAST(parsed: Parsed[Seq[ELK.AST]]): (Map[String, (Seq[ELK.Instrument], Map[String, String])], ArrayBuffer[ELK.Instrument]) = {
    var functions = Map[String, (Seq[ELK.Instrument], Map[String, String])]()
    var instruments = ArrayBuffer[ELK.Instrument]()
    parsed match {
      case Success(f, state) =>
        f.foreach {
          case i: ELK.Instrument =>
            instruments += i
        }
      case f: Failure =>
        Console.err.println("Invalid Command: " + f.msg)
        println
    }
    (functions, instruments)
  }
}

object ELK {
  sealed trait AST extends Any {
    def value: Any
  }

  case class Instrument(value: (String, Option[Seq[Val] => String], Seq[Val])) extends AnyVal with AST
}


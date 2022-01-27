package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._
import org.apache.commons.lang3.StringEscapeUtils

class JsonParser {
  val StringChars: NamedFunction[Char, Boolean] = NamedFunction(!"\"".contains(_: Char), "StringChars")
  val AlphaChars: NamedFunction[Char, Boolean] = NamedFunction(!"\"\\?".contains(_: Char), "StringChars")
  val NotNewlineChars: NamedFunction[Char, Boolean] = NamedFunction(!"\n\r\n\r\f".contains(_: Char), "StringChars")
  val CollectionChars: NamedFunction[Char, Boolean] =
    NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")

  def strChars[_: P] = P(CharsWhile(StringChars))

  def noNewlineChars[_: P] = P(CharsWhile(NotNewlineChars))

  def alphaChars[_: P] = P(CharsWhile(AlphaChars))

  def collectionChars[_: P] = P(CharsWhile(CollectionChars))

  def variableChars[_: P] = P(CharIn("a-zA-Z_"))

  def hexDigit[_: P] = P(CharIn("0-9a-fA-F"))

  def actionChars[_: P] = P(CharIn("0-9a-zA-Z$:/_@?%*= ,\\.\\&\\-"))

  def unicodeEscape[_: P] = P("u" ~ hexDigit ~ hexDigit ~ hexDigit ~ hexDigit)

  def escape[_: P] = P("\\" ~ (CharIn("\"/\\bfnrt") | unicodeEscape))

  //val string =
  //P("\"" ~/ (strChars | escape).rep.! ~ "\"").map(i => JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))
  def actionPath[_: P] = P(space ~ actionChars.rep(1).! ~ space).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def quoteString[_: P] = P(space ~ "\"" ~ strChars.rep(0).! ~ "\"" ~ space).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def variableName[_: P] = P(variableChars.rep(1)).!

  def variable[_: P] = P(space ~ "$" ~ variableChars.rep(1).! ~ space).map(JsonCollection.Var)

  //val parameter: P[String] = P(space ~ string ~ ",".? ~ space)
  def strOrVar[_: P] = P(quoteString | variable)

  def Digits = NamedFunction('0' to '9' contains (_: Char), "Digits")

  def digits[_: P] = P(CharsWhile(Digits))

  def exponent[_: P] = P(CharIn("eE") ~ CharIn("+\\-").? ~ digits)

  def fractional[_: P] = P("." ~ digits)

  def integral[_: P] = P("0" | CharIn("1-9") ~ digits.?)

  def number[_: P]: P[JsonCollection.Num] =
    P(CharIn("+\\-").? ~ integral ~ fractional.? ~ exponent.?).!.map(
      x => {
        val double = x.toDouble
        double % 1 match {
          case 0 => JsonCollection.Num(double.toInt)
          case _ => JsonCollection.Num(double)
        }
      }
    )

  def pair[_: P]: P[(JsonCollection.Val, JsonCollection.Val)] =
    P(newlineOrComment ~ (quoteString | variable) ~/ ":" ~ newlineOrComment ~/ jsonExpr)


  def `null`[_: P] = P("null").map(_ => JsonCollection.Null)

  def `false`[_: P] = P("false").map(_ => JsonCollection.False)

  def `true`[_: P] = P("true").map(_ => JsonCollection.True)

  def obj[_: P] =
    P("{" ~/ newlineOrComment ~/ pair.rep(sep = ",") ~ ",".? ~ newlineOrComment ~ "}").map(JsonCollection.Obj(_: _*))

  def tuple[_: P] =
    P("(" ~/ newlineOrComment ~/ jsonExpr.rep(sep = ",") ~ ",".? ~ newlineOrComment ~ ")").map(JsonCollection.Arr(_: _*))


  def array[_: P] =
    P("[" ~/ newlineOrComment ~/ jsonExpr.rep(sep = ",") ~ ",".? ~ newlineOrComment ~ "]").map(JsonCollection.Arr(_: _*))


  def colon[_: P] = P(space ~ ":" ~ space)

  def jsonExpr[_: P]: P[JsonCollection.Val] = P(
    newlineOrComment ~ (obj | array | tuple | quoteString | `true` | `false` | `null` | number | variable) ~ newlineOrComment
  )

  def ioParser[_: P] = P(jsonExpr.rep(1))

  def space[_: P] = P(CharsWhileIn(" \r\n\t", 0))

  def newlineChars[_: P] = P(" " | "\n" | "\r\n" | "\r" | "\f" | "\t")

  def newline[_: P] = P(newlineChars).rep(1)

  def commentString[_: P] = P("#" ~/ noNewlineChars.rep(0) ~/ (newlineChars | End))

  def newlineOrComment[_: P] = P(newlineChars | commentString).rep(0)
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T): V = f(t)

  override def toString(): String = name
}

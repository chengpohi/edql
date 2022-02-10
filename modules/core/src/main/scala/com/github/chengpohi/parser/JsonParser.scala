package com.github.chengpohi.parser

import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._
import org.apache.commons.lang3.StringEscapeUtils


class JsonParser extends InterceptFunction {
  val StringChars: NamedFunction[Char, Boolean] = NamedFunction(!"\"".contains(_: Char), "StringChars")
  val AlphaChars: NamedFunction[Char, Boolean] = NamedFunction(!"\"\\?".contains(_: Char), "StringChars")
  val NewlineChars: NamedFunction[Char, Boolean] = NamedFunction("\n\r\n\r\f".contains(_: Char), "StringChars")
  val NotNewlineChars: NamedFunction[Char, Boolean] = NamedFunction(!"\n\r\n\r\f".contains(_: Char), "StringChars")
  val CollectionChars: NamedFunction[Char, Boolean] =
    NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")

  def newline[_: P] = P(CharsWhile(NewlineChars))

  def strChars[_: P] = P(CharsWhile(StringChars))

  def notNewlineChars[_: P] = P(CharsWhile(NotNewlineChars))

  def alphaChars[_: P] = P(CharsWhile(AlphaChars))

  def collectionChars[_: P] = P(CharsWhile(CollectionChars))

  def variableChars[_: P] = P(CharIn("a-zA-Z_"))

  def hexDigit[_: P] = P(CharIn("0-9a-fA-F"))

  def actionChars[_: P] = P(CharIn("0-9a-zA-Z$:/_@?%*= ,\\.\\&\\-"))

  def unicodeEscape[_: P] = P("u" ~ hexDigit ~ hexDigit ~ hexDigit ~ hexDigit)

  def escape[_: P] = P("\\" ~ (CharIn("\"/\\bfnrt") | unicodeEscape))

  //val string =
  //P("\"" ~/ (strChars | escape).rep.! ~ "\"").map(i => JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))
  def actionPath[_: P] = P(WS ~ actionChars.rep(1).! ~ WS).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def quoteString[_: P] = P(WS ~ "\"" ~ strChars.rep(0).! ~ "\"" ~ WS).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def variableName[_: P] = P(variableChars.rep(1)).!

  def variable[_: P] = P(WS ~ "$" ~ variableChars.rep(1).! ~ WS).map(JsonCollection.Var)

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
    P(WS ~ (quoteString | variable) ~/ ":" ~ WS ~/ jsonExpr)

  def `null`[_: P] = P("null").map(_ => JsonCollection.Null)

  def `false`[_: P] = P("false").map(_ => JsonCollection.False)

  def `true`[_: P] = P("true").map(_ => JsonCollection.True)

  def obj[_: P] =
    P("{" ~/ WS ~/ pair.rep(sep = ",") ~ ",".? ~ WS ~ "}").map(JsonCollection.Obj(_: _*))

  def tuple[_: P] =
    P("(" ~/ WS ~/ jsonExpr.rep(sep = ",") ~ ",".? ~ WS ~ ")").map(JsonCollection.Arr(_: _*))

  def array[_: P] =
    P("[" ~/ WS ~/ jsonExpr.rep(sep = ",") ~ ",".? ~ WS ~ "]").map(JsonCollection.Arr(_: _*))

  def colon[_: P] = P(WS ~ ":" ~ WS)

  def fun[_: P]: P[JsonCollection.Fun] =
    P(WS ~ variableName ~ WS ~ "(" ~ (jsonExpr | fun).rep(sep = WS ~ "," ~ WS) ~ ")" ~ WS.?)
      .map(c => {
        JsonCollection.Fun((c._1, c._2))
      })

  def parens[_: P]: P[JsonCollection.ArithTree] = P(WS ~ "(" ~/ WS ~/ addSub ~ WS ~ ")" ~ WS)

  def factor[_: P]: P[JsonCollection.ArithTree] = P(quoteString | number | parens) map {
    case n: JsonCollection.Num => JsonCollection.ArithTree((n, None, None))
    case s: JsonCollection.Str => JsonCollection.ArithTree((s, None, None))
    case p: JsonCollection.ArithTree => p
  }

  def divMulArith(h: JsonCollection.ArithTree, value: Seq[(String, JsonCollection.Val)]): JsonCollection.ArithTree = {
    value match {
      case Seq(i) => {
        JsonCollection.ArithTree(h, Some(i._1), Some(i._2))
      }
      case i => {
        val arith = JsonCollection.ArithTree((h, Some(i.head._1), Some(i.head._2)))
        divMulArith(arith, i.drop(1))
      }
    }
  }

  def divMul[_: P]: P[JsonCollection.ArithTree] = P(WS ~ factor ~ WS ~ (WS ~ CharIn("*\\/").! ~ WS ~/ factor ~ WS).rep ~ WS).map(v => {
    v._2 match {
      case Seq() => {
        JsonCollection.ArithTree(v._1, None, None)
      }
      case Seq(i) => {
        JsonCollection.ArithTree(v._1, Some(i._1), Some(i._2))
      }
      case i => {
        val h = JsonCollection.ArithTree(v._1, Some(i.head._1), Some(i.head._2))
        divMulArith(h, i.drop(1))
      }
    }
  })

  // 3 + 2 * 5
  def addSub[_: P]: P[JsonCollection.ArithTree] = P(WS ~ divMul ~ WS ~ (WS ~ CharIn("+\\-").! ~ WS ~/ divMul ~ WS).rep).map(v => {
    v._2 match {
      case Seq() => {
        JsonCollection.ArithTree(v._1, None, None)
      }
      case Seq(i) => {
        JsonCollection.ArithTree(v._1, Some(i._1), Some(i._2))
      }
      case i => {
        val h = JsonCollection.ArithTree(v._1, Some(i.head._1), Some(i.head._2))
        divMulArith(h, i.drop(1))
      }
    }
  })

  def jsonExpr[_: P]: P[JsonCollection.Val] = P(
    WS ~ (addSub | obj | array | tuple | quoteString | `true` | `false` | `null` | number | variable | fun) ~ WS
  )

  def ioParser[_: P] = P(jsonExpr.rep(1))

  def commentChars[_: P] = P("#" ~/ notNewlineChars.rep(0) ~/ End.?)

  def WS[_: P] = P(" " | newline | commentChars).rep
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T): V = f(t)

  override def toString(): String = name
}

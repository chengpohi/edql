package com.github.chengpohi.parser

import com.github.chengpohi.parser.Lexical._
import com.github.chengpohi.parser.collection.JsonCollection
import fastparse.NoWhitespace._
import fastparse._
import org.apache.commons.lang3.StringEscapeUtils


class JsonParser extends InterceptFunction {
  //val string =
  //P("\"" ~/ (strChars | escape).rep.! ~ "\"").map(i => JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))
  def actionPath[_: P] = P(WS ~ actionChars.rep(1).! ~ WS).map(i =>
    JsonCollection.Str(StringEscapeUtils.unescapeJava(i)))

  def number[_: P]: P[JsonCollection.Num] =
    P(CharIn("+\\-").? ~ integral ~ fractional.? ~ exponent.?).!.map(
      x => {
        val double = x.toDouble
        JsonCollection.Num(double)
      }
    )

  def pair[_: P]: P[(JsonCollection.Val, JsonCollection.Val)] =
    P(WS ~ (stringLiteral | variable) ~/ ":" ~ WS ~/ jsonExpr)

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

  def factor[_: P]: P[JsonCollection.ArithTree] = P(stringLiteral | number | parens) map {
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
    WS ~ (addSub | obj | array | tuple | stringLiteral | `true` | `false` | `null` | number | fun | variable) ~ WS
  )

  def ioParser[_: P] = P(jsonExpr.rep(1))
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T): V = f(t)

  override def toString(): String = name
}

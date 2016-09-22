package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection

import fastparse.noApi._

/**
 * elasticshell
 * Created by chengpohi on 2/1/16.
 */
class CollectionParser extends Basic{
  import WhitespaceApi._

  val StringChars = NamedFunction(!"\"".contains(_: Char), "StringChars")
  val AlphaChars = NamedFunction(!"\"\\?".contains(_: Char), "StringChars")
  val CollectionChars = NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")

  val strChars = P(CharsWhile(StringChars))
  val alphaChars = P(CharsWhile(AlphaChars))
  val collectionChars = P(CharsWhile(CollectionChars))
  val variableChars = P(CharIn('a' to 'z', 'A' to 'Z'))
  val string = P("\"" ~ strChars.rep(1).! ~ "\"").map(JsonCollection.Str)
  val variable = P(variableChars.rep(1)).!.map(s => "$" + s).map(JsonCollection.Str)
  //val parameter: P[String] = P(space ~ string ~ ",".? ~ space)
  val strOrVar = P(string | variable)
  val number = P(CharIn('0' to '9').rep(1)).!.map(x => JsonCollection.Num(x.toDouble))
  val pair = P(string.map(_.value) ~/ ":" ~/ jsonExpr)
  val obj = P("{" ~/ pair.rep(sep = ",".~/) ~ "}").map(JsonCollection.Obj(_: _*))

  val `null` = P("null").map(_ => JsonCollection.Null)
  val `false` = P("false").map(_ => JsonCollection.False)
  val `true` = P("true").map(_ => JsonCollection.True)


  val tuple = P("(" ~ jsonExpr.rep(1, sep = ",".~/) ~ ")").map(JsonCollection.Arr(_: _*))
  val array = P("[" ~ jsonExpr.rep(1, sep = ",".~/) ~ "]").map(JsonCollection.Arr(_: _*))

  val jsonExpr: P[JsonCollection.Val] = P(obj | array | tuple | string | `true` | `false` | `null` | number)
  val ioParser: P[Seq[JsonCollection.Val]] = P(jsonExpr.rep(1))
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T): V = f(t)

  override def toString(): String = name
}

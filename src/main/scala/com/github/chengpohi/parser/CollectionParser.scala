package com.github.chengpohi.parser

import com.github.chengpohi.collection.Js

/**
 * elasticshell
 * Created by chengpohi on 2/1/16.
 */
class CollectionParser {

  import fastparse.all._

  val StringChars = NamedFunction(!"\"\\".contains(_: Char), "StringChars")
  val CollectionChars = NamedFunction(!"[],()\"\\".contains(_: Char), "CollectionChars")
  val space = P(CharsWhile(" \r\n".contains(_)).?)

  val strChars = P(CharsWhile(StringChars))
  val collectionChars = P(CharsWhile(CollectionChars))
  val variableChars = P(CharIn('a' to 'z', 'A' to 'Z'))
  val string = P("\"" ~ strChars.rep(1).! ~ "\"").map(Js.Str)
  val variable = P(variableChars.rep(1)).!.map(s => "$" + s).map(Js.Str)
  //val parameter: P[String] = P(space ~ string ~ ",".? ~ space)
  val strOrVar = P(string | variable)
  val number = P(CharIn('0' to '9').rep(1)).!.map(x => Js.Num(x.toDouble))
  val pair = P(space ~/ string.map(_.value) ~/ space ~/ ":" ~/ jsonExpr)
  val obj = P("{" ~ space ~/ pair.rep(sep = ",".~/) ~ space ~ "}").map(Js.Obj(_: _*))

  val `null` = P("null").map(_ => Js.Null)
  val `false` = P("false").map(_ => Js.False)
  val `true` = P("true").map(_ => Js.True)


  val tuple = P("(" ~ jsonExpr.rep(1, sep = space ~ "," ~ space.~/) ~ ")").map(Js.Arr(_: _*))
  val array = P("[" ~ jsonExpr.rep(1, sep = space ~ "," ~ space.~/) ~ "]").map(Js.Arr(_: _*))
  //val collection = P(space ~ (obj | tuple | array | number | strOrVar) ~ space)

  val jsonExpr: P[Js.Val] = P(
    space ~ (obj | array | tuple | string | `true` | `false` | `null` | number) ~ space
  )
  val ioParser: P[Seq[Js.Val]] = P(jsonExpr.rep(1, sep = space))
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T) = f(t)

  override def toString() = name
}

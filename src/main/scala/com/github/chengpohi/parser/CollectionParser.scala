package com.github.chengpohi.parser

/**
 * elasticshell
 * Created by chengpohi on 2/1/16.
 */
class CollectionParser {

  import fastparse.all._

  val StringChars = NamedFunction(!"\"\\".contains(_: Char), "StringChars")
  val CollectionChars = NamedFunction(!",()\"\\".contains(_: Char), "CollectionChars")
  val space = P(CharsWhile(" \r\n".contains(_)).?)

  val strChars = P(CharsWhile(StringChars))
  val collectionChars = P(CharsWhile(CollectionChars))
  val variableChars = P(CharIn('a' to 'z', 'A' to 'Z'))
  val strParameter: P[String] = P("\"" ~ strChars.rep(1).! ~ "\"")
  val variable: P[String] = P(variableChars.rep(1)).!.map(s => "$" + s)
  val parameter: P[String] = P(space ~ strParameter ~ ",".? ~ space)
  val strOrVar: P[String] = P(strParameter | variable)

  val tuple: P[Seq[String]] = P("(" ~ collectionChars.!.rep(1, sep = ",".~/) ~ ")")
  val array: P[Seq[Seq[String]]] = P("[" ~ tuple.rep(1, sep = ",".~/) ~ "]")
  val collection = P(space ~ (tuple | array) ~ space)
}

case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
  def apply(t: T) = f(t)

  override def toString() = name
}


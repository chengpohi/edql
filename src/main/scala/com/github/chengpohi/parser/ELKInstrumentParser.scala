package com.github.chengpohi.parser

/**
 * elasticservice
 * Created by chengpohi on 1/18/16.
 */
class ELKInstrumentParser {

  import fastparse.all._

  val StringChars = NamedFunction(!"\"\\".contains(_: Char), "StringChars")

  val strChars = P(CharsWhile(StringChars))
  val space = P(CharsWhile(" \r\n".contains(_)).?)
  val strName = P(CharIn('a' to 'z', 'A' to 'Z'))
  val strParameter: P[String] = P("\"" ~ strChars.rep.! ~ "\"")
  val variable: P[String] = P(strName.rep.!).map(s => "$" + s)
  val parameter: P[String] = P(space ~ strParameter ~ ",".? ~ space)

  val status = P("health").map(s => ("health", Some(ELKCommand.h), Seq()))
  val count = P("count" ~ space ~ (strParameter | variable)).map(c =>
    ("count", Some(ELKCommand.c), Seq(c)))
  val delete = P("delete" ~ space ~ (strParameter | variable)).map(c =>
    ("delete", Some(ELKCommand.d), Seq(c)))
  val query = P("query" ~ space ~ (strParameter | variable)).map(c =>
    ("query", Some(ELKCommand.q), Seq(c)))
  val reindex = P("reindex" ~ space ~ (strParameter | variable).rep(4, sep = " ")).map(
    c => ("reindex", Some(ELKCommand.r), c))
  val index = P("index" ~ space ~ (strParameter | variable).rep(3, sep = " ")).map(
    c => ("index", Some(ELKCommand.i), c)
  )
  val createIndex = P("createIndex" ~ space ~ (strParameter | variable)).map(
    c => ("createIndex", Some(ELKCommand.ci), Seq(c))
  )
  val update = P("update" ~ space ~ (strParameter | variable).rep(3, sep = " ")).map(c =>
    ("query", Some(ELKCommand.u), c))
  val analysis = P("analysis" ~ space ~ (strParameter | variable).rep(2, sep=" ")).map(c =>
    ("analysis", Some(ELKCommand.a), c))
  val functionInstrument = P(strName.rep.! ~ "(" ~/ parameter.rep ~ ")").map(f => (f._1, None, f._2))
}

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
  val strParameter: P[String] = P("\"" ~ strChars.rep(1).! ~ "\"")
  val variable: P[String] = P(strName.rep(1)).!.map(s => "$" + s)
  val parameter: P[String] = P(space ~ strParameter ~ ",".? ~ space)
  val strOrVar: P[String] = P(strParameter | variable)

  val status = P("health").map(s => ("health", Some(ELKCommand.h), Seq()))
  val count = P("count" ~ space ~/ strOrVar).map(c =>
    ("count", Some(ELKCommand.c), Seq(c)))
  val delete = P("delete" ~ space ~/ strOrVar ~ space ~ strOrVar.?).map(c =>
    ("delete", Some(ELKCommand.d), Seq(c._1, c._2.getOrElse("*"))))
  val query = P("query" ~ space ~/ strOrVar ~ space ~ strOrVar.?).map(c =>
    ("query", Some(ELKCommand.q), Seq(c._1, c._2.getOrElse("*"))))
  val reindex = P("reindex" ~ space ~/ strOrVar.rep(4, sep = " ")).map(
    c => ("reindex", Some(ELKCommand.r), c))
  val index = P("index" ~ space ~/ strOrVar.rep(3, sep = " ") ~ space ~ strOrVar.?).map(
    c => ("index", Some(ELKCommand.i), c._1 :+ c._2.getOrElse("*"))
  )
  val createIndex = P("createIndex" ~ space ~/ strOrVar).map(
    c => ("createIndex", Some(ELKCommand.ci), Seq(c))
  )
  val getMapping = P(space ~ strOrVar ~ space ~ "mapping").map(
    c => ("getMapping", Some(ELKCommand.gm), Seq(c))
  )
  val update = P("update" ~ space ~/ strOrVar.rep(3, sep = " ")).map(c =>
    ("query", Some(ELKCommand.u), c))
  val analysis = P("analysis" ~ space ~/ strOrVar.rep(2, sep = " ")).map(c =>
    ("analysis", Some(ELKCommand.a), c))
  val getDocById = P("get" ~ space ~/ strOrVar.rep(3, sep = " ")).map(c =>
    ("getDocById", Some(ELKCommand.gd), c))
  val mapping = P("mapping" ~ space ~/ strOrVar.rep(3, sep = " ")).map(c =>
    ("mapping", Some(ELKCommand.m), c))

  val extractJSON = P(space ~ "\\\\" ~ space ~ strOrVar ~ space).map(c =>
    ("extract", ELKCommand.findJSONElements(c))
  )
  val beauty = P(space ~ "beauty" ~ space).map(c =>
    ("beauty", ELKCommand.beautyJson)
  )

  val functionInstrument = P(strName.rep(1).! ~ "(" ~/ parameter.rep ~ ")").map(f => (f._1, None, f._2))

  val instrument = P(space ~ (status | count | delete | query | reindex
    | index | createIndex | update | analysis | getMapping | getDocById | mapping | functionInstrument)
    ~ space ~ (extractJSON).? ~ space).map(i => i._4 match {
    case Some((name, extractFunction)) if i._2.isDefined => {
      val f: Seq[String] => String = i._2.get
      val fComponent = (f andThen extractFunction)(_)
      ELK.Instrument(i._1, Some(fComponent), i._3)
    }
    case _ => ELK.Instrument(i._1, i._2, i._3)
  })
}

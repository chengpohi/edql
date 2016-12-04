package com.github.chengpohi.parser

import com.github.chengpohi.api.dsl.Definition
import com.github.chengpohi.collection.JsonCollection
import com.github.chengpohi.collection.JsonCollection.{Str, Val}
import fastparse.noApi._

/**
  * elasticservice
  * Created by chengpohi on 1/18/16.
  */
class ELKInstructionParser(val elkCommand: ELKCommand, val parserUtils: ParserUtils) extends CollectionParser {
  type INSTRUMENT_TYPE = (String, Option[Seq[Val] => Definition[_]], Seq[Val])

  import WhitespaceApi._

  val help = P(alphaChars.rep(1).! ~ "?")
    .map(JsonCollection.Str)
    .map(s => ("help", Some(parserUtils.help), Seq(s)))
  val health = P("health").map(s => Instruction("health", elkCommand.health, Seq(Str(""))))
  val shutdown = P("shutdown").map(s => Instruction("shutdown", elkCommand.shutdown, Seq(Str(""))))
  val count = P("count" ~/ ioParser).map(c => Instruction("count", elkCommand.count, c))
  //memory, jvm, nodes, cpu etc
  val clusterStats = P("cluster stats").map(s => Instruction("clusterStats", elkCommand.clusterStats, Seq()))
  //indices, aliases, restore, snapshots, routing nodes etc
  val clusterState = P("cluster state").map(s => Instruction("clusterState", elkCommand.getIndices, Seq()))
  val indicesStats = P("indices stats").map(s => Instruction("indiciesStats", elkCommand.indicesStats, Seq()))
  val nodeStats = P("node stats").map(s => Instruction("nodeStats", elkCommand.nodeStats, Seq()))
  val clusterSettings = P("cluster settings").map(s => Instruction("clusterSettings", elkCommand.clusterSettings, Seq()))
  val nodeSettings = P("node settings").map(s => Instruction("nodeSettings", elkCommand.nodeSettings, Seq()))
  val indexSettings = P(ioParser ~ "settings").map(s => Instruction("nodeSettings", elkCommand.indexSettings, s))
  val pendingTasks = P("pending tasks").map(s => Instruction("pendingTasks", elkCommand.pendingTasks, Seq()))
  val waitForStatus = P("wait for status" ~ ioParser).map(s => Instruction("pendingTasks", elkCommand.waitForStatus, s))
  val deleteDoc = P("delete" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~ strOrVar)
    .map(c => Instruction("delete", elkCommand.deleteDoc, Seq(c._1, c._2, c._3)))
  val deleteIndex = P("delete" ~ "index" ~/ strOrVar).map(c => Instruction("delete", elkCommand.deleteIndex, Seq(c)))

  val termQuery = P("term" ~ "query" ~/ ioParser).map(c => Instruction("termQuery", elkCommand.query, c))
  val joinSearch = P("join" ~ strOrVar ~ "/" ~ strOrVar ~ "by" ~ strOrVar)
    .map(c => Instruction("joinQuery", elkCommand.joinQuery, Seq(c._1, c._2, c._3)))
  val matchQuery = P("match" ~/ jsonExpr)
    .map(c => Instruction("matchQuery", elkCommand.matchQuery, Seq(c)))
  val search: P[Instruction] = P("search" ~ "in" ~/ strOrVar ~ ("/" ~ strOrVar ~ (matchQuery | joinSearch).?).?)
    .map(c => c._2 match {
      case None => Instruction("query", elkCommand.query, Seq(c._1))
      case Some((f, None)) => Instruction("query", elkCommand.query, Seq(c._1, f))
      case Some((f, Some(t))) => Instruction(t.name, t.f, Seq(c._1, f) ++ t.params)
    })

  val reindex = P("reindex" ~ "into" ~ strOrVar ~ "/" ~/ strOrVar ~ "from" ~/ strOrVar ~ "fields" ~/ jsonExpr)
    .map(c => Instruction("reindex", elkCommand.reindexIndex, Seq(c._1, c._2, c._3, c._4)))
  val index = P("index" ~ "into" ~/ strOrVar ~ "/" ~ strOrVar ~/ "fields" ~ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(c => Instruction("index", elkCommand.createDoc, Seq(c._1, c._2, c._3) ++ c._4.toSeq))
  val bulkIndex = P("bulk index" ~/ ioParser).map(c => Instruction("bulkIndex", elkCommand.bulkIndex, c))
  val updateMapping = P("update mapping" ~/ ioParser).map(c => Instruction("umapping", elkCommand.updateMapping, c))
  val update = P("update" ~ "on" ~/ strOrVar ~ "/" ~ strOrVar ~ "fields" ~/ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(c => {
      c._4 match {
        case None => Instruction("update", elkCommand.bulkUpdateDoc, Seq(c._1, c._2, c._3))
        case Some(id) => Instruction("update", elkCommand.updateDoc, Seq(c._1, c._2, c._3) ++ c._4.toSeq)
      }
    })
  val createIndex = P("create" ~ "index" ~/ strOrVar).map(c => Instruction("createIndex", elkCommand.createIndex, Seq(c)))
  val getMapping = P(strOrVar ~ "mapping").map(c => Instruction("getMapping", elkCommand.getMapping, Seq(c)))
  val analysis = P("analysis" ~/ strOrVar ~/ "by" ~/ strOrVar).map(c => Instruction("analysis", elkCommand.analysisText, Seq(c._1, c._2)))
  val createAnalyzer = P("create analyzer" ~/ ioParser).map(c => Instruction("createAnalyzer", elkCommand.createAnalyzer, c))
  val getDocById = P("get" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~/ strOrVar)
    .map(c => Instruction("getDocById", elkCommand.getDocById, Seq(c._1, c._2, c._3)))
  val mapping = P("mapping" ~/ ioParser).map(c => Instruction("mapping", elkCommand.mapping, c))
  val avgAggs = P("avg" ~/ strOrVar).map(c => Instruction("avgAggs", elkCommand.aggsCount, Seq(c)))
  val termsAggs = P("term" ~/ strOrVar).map(c => Instruction("termAggs", elkCommand.aggsTerm, Seq(c)))
  val histAggs = P("hist" ~/ strOrVar ~ "interval" ~/ strOrVar ~/ "field" ~ strOrVar)
    .map(c => Instruction("histAggs", elkCommand.histAggs, Seq(c._1, c._2, c._3)))
  val aggs = P("aggs in" ~/ strOrVar ~ "/" ~ strOrVar ~/ (avgAggs | termsAggs | histAggs))
    .map(c => Instruction(c._3.name, c._3.f, Seq(c._1, c._2) ++ c._3.params))
  val alias = P("alias" ~/ ioParser).map(c => Instruction("alias", elkCommand.alias, c))
  val createRepository = P("create repository" ~/ ioParser).map(c => Instruction("createRepository", elkCommand.createRepository, c))
  val createSnapshot = P("create snapshot " ~/ ioParser).map(c => Instruction("createSnapshot", elkCommand.createSnapshot, c))
  val deleteSnapshot = P("delete snapshot " ~/ ioParser).map(c => Instruction("deleteSnapshot", elkCommand.deleteSnapshot, c))
  val getSnapshot = P("get snapshot " ~/ ioParser).map(c => Instruction("getSnapshot", elkCommand.getSnapshot, c))
  val restoreSnapshot = P("restore snapshot " ~/ ioParser).map(c => Instruction("restoreSnapshot", elkCommand.restoreSnapshot, c))
  val closeIndex = P("close index" ~/ ioParser).map(c => Instruction("closeIndex", elkCommand.closeIndex, c))
  val openIndex = P("open index" ~/ ioParser).map(c => Instruction("openIndex", elkCommand.openIndex, c))
  val dumpIndex = P("dump index" ~/ strOrVar ~/ ">" ~/ strChars.rep(1).!)
    .map(c => Instruction("dumpIndex", elkCommand.dumpIndex, Seq(c._1, JsonCollection.Str(c._2))))
  //val extractJSON = P("\\\\" ~ strOrVar).map(c => ("extract", findJSONElements(c.value)))
  //val beauty = P("beauty").map(c => ("beauty", beautyJson))

  val instrument: P[Instruction] = P(health | shutdown | clusterStats | indicesStats | nodeStats | pendingTasks | waitForStatus
    | clusterSettings | nodeSettings | indexSettings | clusterState
    | restoreSnapshot | deleteSnapshot | createSnapshot | getSnapshot | createRepository
    | deleteDoc | deleteIndex
    | search | termQuery | getDocById
    | reindex | index | bulkIndex | createIndex | closeIndex | openIndex | dumpIndex
    | updateMapping | update | analysis | aggs | createAnalyzer
    | getMapping | mapping
    | alias | count)

  case class Instruction(name: String, f: Seq[Val] => Definition[_], params: Seq[Val])
}

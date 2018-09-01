package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection
import com.github.chengpohi.collection.JsonCollection.Str
import fastparse.noApi._


trait EQLInstructionParser extends CollectionParser {
  val interceptFunction: InterceptFunction

  import WhitespaceApi._

  val help = P(alphaChars.rep(1).! ~ "?")
    .map(JsonCollection.Str)
    .map(s => ("help", Some(interceptFunction.help), Seq(s)))
  val health = P("health").map(
    s =>
      interceptFunction
        .Instruction("health", interceptFunction.health, Seq(Str(""))))
  val shutdown = P("shutdown").map(
    s =>
      interceptFunction
        .Instruction("shutdown", interceptFunction.shutdown, Seq(Str(""))))
  val count = P("count" ~/ ioParser).map(c =>
    interceptFunction.Instruction("count", interceptFunction.count, c))
  //memory, jvm, nodes, cpu etc
  val clusterStats = P("cluster stats").map(
    s =>
      interceptFunction
        .Instruction("clusterStats", interceptFunction.clusterStats, Seq()))
  val catNodes = P("cat nodes").map(
    s =>
      interceptFunction
        .Instruction("catNodes", interceptFunction.catNodes, Seq()))
  val catAllocation = P("cat allocation").map(
    s =>
      interceptFunction
        .Instruction("catNodes", interceptFunction.catAllocation, Seq()))
  val catMaster = P("cat master").map(
    s =>
      interceptFunction
        .Instruction("catNodes", interceptFunction.catMaster, Seq()))
  val catIndices = P("cat indices").map(
    s =>
      interceptFunction
        .Instruction("catNodes", interceptFunction.catIndices, Seq()))
  val catShards = P("cat shards").map(
    s =>
      interceptFunction
        .Instruction("catNodes", interceptFunction.catShards, Seq()))
  val clusterHealth = P("cluster health").map(
    s =>
      interceptFunction
        .Instruction("clusterHealth", interceptFunction.clusterHealth, Seq()))
  //indices, aliases, restore, snapshots, routing nodes etc
  val clusterState = P("cluster state").map(
    s =>
      interceptFunction
        .Instruction("clusterState", interceptFunction.getClusterState, Seq()))
  val indicesStats = P("indices stats").map(
    s =>
      interceptFunction
        .Instruction("indiciesStats", interceptFunction.indicesStats, Seq()))
  val nodeStats = P("node stats").map(
    s =>
      interceptFunction
        .Instruction("nodeStats", interceptFunction.nodeStats, Seq()))
  val clusterSettings = P("cluster settings").map(
    s =>
      interceptFunction.Instruction("clusterSettings",
        interceptFunction.clusterSettings,
        Seq()))
  val nodeSettings = P("node settings").map(
    s =>
      interceptFunction
        .Instruction("nodeSettings", interceptFunction.nodeSettings, Seq()))
  val indexSettings = P(ioParser ~ "settings").map(
    s =>
      interceptFunction
        .Instruction("nodeSettings", interceptFunction.indexSettings, s))
  val pendingTasks = P("pending tasks").map(
    s =>
      interceptFunction
        .Instruction("pendingTasks", interceptFunction.pendingTasks, Seq()))
  val waitForStatus = P("wait for status" ~ ioParser).map(
    s =>
      interceptFunction
        .Instruction("pendingTasks", interceptFunction.waitForStatus, s))
  val deleteDoc =
    P("delete" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~ strOrVar)
      .map(
        c =>
          interceptFunction.Instruction("delete",
            interceptFunction.deleteDoc,
            Seq(c._1, c._2, c._3)))
  val deleteIndex = P("delete" ~ "index" ~/ strOrVar).map(
    c =>
      interceptFunction
        .Instruction("delete", interceptFunction.deleteIndex, Seq(c)))

  val termQuery = P("term" ~ "query" ~/ ioParser).map(c =>
    interceptFunction.Instruction("termQuery", interceptFunction.query, c))
  val joinSearch = P("join" ~ strOrVar ~ "/" ~ strOrVar ~ "by" ~ strOrVar)
    .map(
      c =>
        interceptFunction.Instruction("joinQuery",
          interceptFunction.joinQuery,
          Seq(c._1, c._2, c._3)))
  val matchQuery = P("match" ~/ jsonExpr)
    .map(
      c =>
        interceptFunction
          .Instruction("matchQuery", interceptFunction.matchQuery, Seq(c)))
  val search: P[interceptFunction.Instruction] = P(
    "search" ~ "in" ~/ strOrVar ~ ("/" ~ strOrVar ~ (matchQuery | joinSearch).?).?)
    .map(c =>
      c._2 match {
        case None =>
          interceptFunction.Instruction("query",
            interceptFunction.query,
            Seq(c._1))
        case Some((f, None)) =>
          interceptFunction.Instruction("query",
            interceptFunction.query,
            Seq(c._1, f))
        case Some((f, Some(t))) =>
          interceptFunction.Instruction(t.name, t.f, Seq(c._1, f) ++ t.params)
      })

  val reindex = P(
    "reindex" ~ "into" ~ strOrVar ~ "/" ~/ strOrVar ~ "from" ~/ strOrVar ~ "fields" ~/ jsonExpr)
    .map(
      c =>
        interceptFunction.Instruction("reindex",
          interceptFunction.reindexIndex,
          Seq(c._1, c._2, c._3, c._4)))
  val index = P(
    "index" ~ "into" ~/ strOrVar ~ "/" ~ strOrVar ~/ "fields" ~ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(
      c =>
        interceptFunction.Instruction("index",
          interceptFunction.createDoc,
          Seq(c._1, c._2, c._3) ++ c._4.toSeq))
  val bulkIndex = P("bulk index" ~/ ioParser).map(c =>
    interceptFunction.Instruction("bulkIndex", interceptFunction.bulkIndex, c))
  val updateMapping = P("update mapping" ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("umapping", interceptFunction.updateMapping, c))
  val update = P(
    "update" ~ "on" ~/ strOrVar ~ "/" ~ strOrVar ~ "fields" ~/ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(c => {
      c._4 match {
        case None =>
          interceptFunction.Instruction("update",
            interceptFunction.bulkUpdateDoc,
            Seq(c._1, c._2, c._3))
        case Some(id) =>
          interceptFunction.Instruction("update",
            interceptFunction.updateDoc,
            Seq(c._1, c._2, c._3) ++ c._4.toSeq)
      }
    })
  val createIndex = P("create" ~ "index" ~/ strOrVar).map(
    c =>
      interceptFunction
        .Instruction("createIndex", interceptFunction.createIndex, Seq(c)))
  val getMapping = P(strOrVar ~ "mapping").map(
    c =>
      interceptFunction
        .Instruction("getMapping", interceptFunction.getMapping, Seq(c)))
  val analysis = P("analysis" ~/ strOrVar ~/ "by" ~/ strOrVar).map(c =>
    interceptFunction
      .Instruction("analysis", interceptFunction.analysisText, Seq(c._1, c._2)))
  val createAnalyzer = P("create analyzer" ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("createAnalyzer", interceptFunction.createAnalyzer, c))
  val getDocById =
    P("get" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~/ strOrVar)
      .map(
        c =>
          interceptFunction.Instruction("getDocById",
            interceptFunction.getDocById,
            Seq(c._1, c._2, c._3)))
  val mapping = P("mapping" ~/ ioParser).map(c =>
    interceptFunction.Instruction("mapping", interceptFunction.mapping, c))
  val avgAggs = P("avg" ~/ strOrVar).map(
    c =>
      interceptFunction
        .Instruction("avgAggs", interceptFunction.aggsCount, Seq(c)))
  val termsAggs = P("term" ~/ strOrVar).map(
    c =>
      interceptFunction
        .Instruction("termAggs", interceptFunction.aggsTerm, Seq(c)))
  val histAggs =
    P("hist" ~/ strOrVar ~ "interval" ~/ strOrVar ~/ "field" ~ strOrVar)
      .map(
        c =>
          interceptFunction.Instruction("histAggs",
            interceptFunction.histAggs,
            Seq(c._1, c._2, c._3)))
  val aggs = P(
    "aggs in" ~/ strOrVar ~ "/" ~ strOrVar ~/ (avgAggs | termsAggs | histAggs))
    .map(c =>
      interceptFunction
        .Instruction(c._3.name, c._3.f, Seq(c._1, c._2) ++ c._3.params))
  val alias = P("alias" ~/ ioParser).map(c =>
    interceptFunction.Instruction("alias", interceptFunction.alias, c))
  val createRepository = P("create repository" ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("createRepository", interceptFunction.createRepository, c))
  val createSnapshot = P("create snapshot " ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("createSnapshot", interceptFunction.createSnapshot, c))
  val deleteSnapshot = P("delete snapshot " ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("deleteSnapshot", interceptFunction.deleteSnapshot, c))
  val getSnapshot = P("get snapshot " ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("getSnapshot", interceptFunction.getSnapshot, c))
  val restoreSnapshot = P("restore snapshot " ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("restoreSnapshot", interceptFunction.restoreSnapshot, c))
  val closeIndex = P("close index" ~/ ioParser).map(
    c =>
      interceptFunction
        .Instruction("closeIndex", interceptFunction.closeIndex, c))
  val openIndex = P("open index" ~/ ioParser).map(c =>
    interceptFunction.Instruction("openIndex", interceptFunction.openIndex, c))
  val dumpIndex = P("dump index" ~/ strOrVar ~/ ">" ~/ strChars.rep(1).!)
    .map(
      c =>
        interceptFunction.Instruction("dumpIndex",
          interceptFunction.dumpIndex,
          Seq(c._1, JsonCollection.Str(c._2))))
  val extractJSON = P("\\\\" ~ strOrVar).map(c => ("extract", c.value))
  //val beauty = P("beauty").map(c => ("beauty", beautyJson))

  val instrument: P[interceptFunction.Instruction] = P(
    (health | shutdown | clusterStats | clusterHealth | indicesStats | nodeStats | pendingTasks | waitForStatus
      | clusterSettings | nodeSettings | indexSettings | clusterState
      | catNodes | catAllocation | catIndices | catMaster | catShards
      | restoreSnapshot | deleteSnapshot | createSnapshot | getSnapshot | createRepository
      | deleteDoc | deleteIndex
      | search | termQuery | getDocById
      | reindex | index | bulkIndex | createIndex | closeIndex | openIndex | dumpIndex
      | updateMapping | update | analysis | aggs | createAnalyzer
      | getMapping | mapping
      | alias | count) ~ extractJSON.?).map(t => {
    t._2 match {
      case Some((name, s)) => {
        val f = interceptFunction.buildExtractDefinition(t._1.f, s)
        t._1.copy(f = f)
      }
      case None => t._1
    }
  })

}

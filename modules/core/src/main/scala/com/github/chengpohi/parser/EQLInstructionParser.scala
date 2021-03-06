package com.github.chengpohi.parser

import fastparse.NoWhitespace._
import fastparse._

trait EQLInstructionParser extends CollectionParser with InterceptFunction {
  def helpP[_: P] = P(alphaChars.rep(1).! ~ "?")
    .map(s => {
      HelpInstruction(Seq(s))
    })

  def healthP[_: P] = P("health").map(
    s => HealthInstruction())


  def shutdown[_: P] = P("shutdown").map(
    _ =>
      ShutdownInstruction())

  def count[_: P] = P("count" ~/ ioParser)
    .map(i => i.head.extract[String])
    .map(c => CountInstruction(c))

  def hostBind[_: P] = P("HOST" ~ space ~ actionPath).map(
    c => EndpointBindInstruction(c.extract[String]))

  def postAction[_: P] = P("POST" ~ space ~ actionPath ~/ newline ~/ jsonExpr).map(
    c => PostActionInstruction(c._1.extract[String], c._2.toJson))

  def getAction[_: P] = P("GET" ~ space ~ actionPath ~/ jsonExpr.?).map(
    c => GetActionInstruction(c._1.extract[String], c._2.map(_.toJson)))

  def deleteAction[_: P] = P("GET" ~ space ~ actionPath ~/ newline ~/ jsonExpr).map(
    c => DeleteActionInstruction(c._1.extract[String], c._2.toJson))

  def putAction[_: P] = P("GET" ~ space ~ actionPath ~/ newline ~/ jsonExpr).map(
    c => PutActionInstruction(c._1.extract[String], c._2.toJson))

  //memory, jvm, nodes, cpu etc
  def clusterStats[_: P] = P("cluster" ~ space ~ "stats").map(
    _ => GetClusterStatsInstruction())

  def catNodes[_: P] = P("cat" ~ space ~ "nodes" ~ newline.?).map(
    _ =>
      CatNodesInstruction())

  def catAllocation[_: P] = P("cat" ~ space ~ "allocation" ~ newline.?).map(
    _ =>
      CatAllocationInstruction())

  def catMaster[_: P] = P("cat" ~ space ~ "master" ~ newline.?).map(
    _ =>
      CatMasterInstruction())

  def catIndices[_: P] = P("cat" ~ space ~ "indices" ~/ newline.?).map(
    _ => CatIndicesInstruction())

  def catShards[_: P] = P("cat" ~ space ~ "shards" ~/ newline.?).map(
    _ =>
      CatShardsInstruction())

  def catCount[_: P] = P("cat" ~ space ~ "count" ~/ newline.?).map(
    _ => CatCountInstruction())

  def catRecovery[_: P] = P("cat" ~ space ~ "recovery" ~/ newline.?).map(
    _ =>
      CatRecoveryInstruction())

  def catPendingTasks[_: P] = P("cat" ~ space ~ "pending_tasks" ~/ newline.?)
    .map(_ =>
      CatPendingInstruction())

  //indices, aliases, restore, snapshots, routing nodes etc
  def clusterState[_: P] = P("cluster" ~ space ~ "state" ~/ newline.?).map(
    _ =>
      GetClusterStateInstruction())

  def clusterSettings[_: P] = P("cluster" ~ space ~ "settings" ~/ newline.?).map(
    s =>
      ClusterSettingsInstruction())

  def indicesStats[_: P] = P("indices" ~ space ~ "stats" ~/ newline.?).map(
    s =>
      IndicesStatsInstruction())

  def nodeStats[_: P] = P("node" ~ space ~ "stats" ~/ newline.?).map(
    s =>
      NodeStatsInstruction())


  def nodeSettings[_: P] = P("node" ~ space ~ "settings" ~/ newline.?).map(
    s =>
      NodeSettingsInstruction())


  def pendingTasks[_: P] = P("pending" ~ space ~ "tasks" ~/ newline.?).map(
    s =>
      PendingTasksInstruction())

  def indexSettings[_: P] = P(ioParser ~ "settings")
    .map(i => i.head.extract[String])
    .map(
      s =>
        IndexSettingsInstruction(s))


  def deleteDoc[_: P] = P("delete" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~ strOrVar)
    .map(
      c =>
        DeleteDocInstruction(c._1.extract[String], c._2.extract[String], c._3.extract[String])
    )

  def deleteIndex[_: P] = P("delete" ~ "index" ~/ strOrVar).map(
    c =>
      DeleteIndexInstruction(c.extract[String]))

  //  val joinSearch = P("join" ~ strOrVar ~ "/" ~ strOrVar ~ "by" ~ strOrVar)
  //    .map(
  //      c =>
  //        JoinQueryInstruction()
  //        interceptFunction.Instruction("joinQuery",
  //          interceptFunction.joinQuery,
  //          Seq(c._1, c._2, c._3)))
  //  val matchQuery = P("match" ~/ jsonExpr)
  //    .map(
  //      c =>
  //        interceptFunction
  //          .Instruction("matchQuery", interceptFunction.matchQuery, Seq(c)))

  def search[_: P] = P(
    "search" ~ "in" ~/ strOrVar)
    .map(c => {
      val indexName = c.extract[String]
      QueryInstruction(indexName, None, Map())
    }
    )

  //
  //  val reindex = P(
  //    "reindex" ~ "into" ~ strOrVar ~ "/" ~/ strOrVar ~ "from" ~/ strOrVar ~ "fields" ~/ jsonExpr)
  //    .map(
  //      c =>
  //        interceptFunction.Instruction("reindex",
  //          interceptFunction.reindexIndex,
  //          Seq(c._1, c._2, c._3, c._4)))
  //  val index = P(
  //    "index" ~ "into" ~/ strOrVar ~ "/" ~ strOrVar ~/ "doc" ~ jsonExpr ~ ("id" ~ strOrVar).?)
  //    .map(
  //      c =>
  //        interceptFunction.Instruction("index",
  //          interceptFunction.createDoc,
  //          Seq(c._1, c._2, c._3) ++ c._4.toSeq))
  //  val bulkIndex = P("bulk index" ~/ ioParser).map(c =>
  //    interceptFunction.Instruction("bulkIndex", interceptFunction.bulkIndex, c))
  //  val updateMapping = P("update mapping" ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("umapping", interceptFunction.updateMapping, c))
  //  val update = P(
  //    "update" ~ "on" ~/ strOrVar ~ "/" ~ strOrVar ~ "doc" ~/ jsonExpr ~ ("id" ~ strOrVar).?)
  //    .map(c => {
  //      c._4 match {
  //        case None =>
  //          interceptFunction.Instruction("update",
  //            interceptFunction.bulkUpdateDoc,
  //            Seq(c._1, c._2, c._3))
  //        case Some(id) =>
  //          interceptFunction.Instruction("update",
  //            interceptFunction.updateDoc,
  //            Seq(c._1, c._2, c._3) ++ c._4.toSeq)
  //      }
  //    })
  //  val createIndex = P("create" ~ "index" ~/ strOrVar).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("createIndex", interceptFunction.createIndex, Seq(c)))
  //  val getMapping = P(strOrVar ~ "mapping").map(
  //    c =>
  //      interceptFunction
  //        .Instruction("getMapping", interceptFunction.getMapping, Seq(c)))
  //  val analysis = P("analysis" ~/ strOrVar ~/ "by" ~/ strOrVar).map(c =>
  //    interceptFunction
  //      .Instruction("analysis", interceptFunction.analysisText, Seq(c._1, c._2)))
  //  val createAnalyzer = P("create analyzer" ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("createAnalyzer", interceptFunction.createAnalyzer, c))
  //  val getDocById =
  //    P("get" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~/ strOrVar)
  //      .map(
  //        c =>
  //          interceptFunction.Instruction("getDocById",
  //            interceptFunction.getDocById,
  //            Seq(c._1, c._2, c._3)))
  //  val mapping = P("mapping" ~/ ioParser).map(c =>
  //    interceptFunction.Instruction("mapping", interceptFunction.mapping, c))
  //  val avgAggs = P("avg" ~/ strOrVar).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("avgAggs", interceptFunction.aggsCount, Seq(c)))
  //  val termsAggs = P("term" ~/ strOrVar).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("termAggs", interceptFunction.aggsTerm, Seq(c)))
  //  val histAggs =
  //    P("hist" ~/ strOrVar ~ "interval" ~/ strOrVar ~/ "field" ~ strOrVar)
  //      .map(
  //        c =>
  //          interceptFunction.Instruction("histAggs",
  //            interceptFunction.histAggs,
  //            Seq(c._1, c._2, c._3)))
  //  val aggs = P(
  //    "aggs in" ~/ strOrVar ~ "/" ~ strOrVar ~/ (avgAggs | termsAggs | histAggs))
  //    .map(c =>
  //      interceptFunction
  //        .Instruction(c._3.name, c._3.f, Seq(c._1, c._2) ++ c._3.params))
  //  val alias = P("alias" ~/ ioParser).map(c =>
  //    interceptFunction.Instruction("alias", interceptFunction.alias, c))
  //  val createRepository = P("create repository" ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("createRepository", interceptFunction.createRepository, c))
  //  val createSnapshot = P("create snapshot " ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("createSnapshot", interceptFunction.createSnapshot, c))
  //  val deleteSnapshot = P("delete snapshot " ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("deleteSnapshot", interceptFunction.deleteSnapshot, c))
  //  val getSnapshot = P("get snapshot " ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("getSnapshot", interceptFunction.getSnapshot, c))
  //  val restoreSnapshot = P("restore snapshot " ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("restoreSnapshot", interceptFunction.restoreSnapshot, c))
  //  val closeIndex = P("close index" ~/ ioParser).map(
  //    c =>
  //      interceptFunction
  //        .Instruction("closeIndex", interceptFunction.closeIndex, c))
  //  val openIndex = P("open index" ~/ ioParser).map(c =>
  //    interceptFunction.Instruction("openIndex", interceptFunction.openIndex, c))
  //  val dumpIndex = P("dump index" ~/ strOrVar ~/ ">" ~/ strChars.rep(1).!)
  //    .map(
  //      c =>
  //        interceptFunction.Instruction("dumpIndex",
  //          interceptFunction.dumpIndex,
  //          Seq(c._1, JsonCollection.Str(c._2))))
  def extractJSON[_: P]: P[(String, String)] = P("\\\\" ~ strOrVar).map(c => ("extract", c.value))

  //val beauty = P("beauty").map(c => ("beauty", beautyJson))

  def instrument[_: P]: P[Seq[Instruction2]] = P(
    (
      healthP | shutdown | clusterStats | indicesStats | nodeStats | pendingTasks
        | search
        | clusterSettings | nodeSettings | indexSettings | clusterState
        | catNodes | catAllocation | catIndices | catMaster | catShards | catCount | catPendingTasks | catRecovery
        | hostBind | postAction | getAction | deleteAction | putAction
        | count
      ).rep
  )
}

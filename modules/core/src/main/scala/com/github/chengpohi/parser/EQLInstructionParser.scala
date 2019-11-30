package com.github.chengpohi.parser

import fastparse.noApi._

trait EQLInstructionParser extends CollectionParser with InterceptFunction {

  import WhitespaceApi._

  val helpP = P(alphaChars.rep(1).! ~ "?")
    .map(s => {
      HelpInstruction(Seq(s))
    })

  val healthP = P("health").map(
    s => HealthInstruction())


  val shutdown = P("shutdown").map(
    _ =>
      ShutdownInstruction())

  val count = P("count" ~/ ioParser)
    .map(i => i.head.extract[String])
    .map(c => CountInstruction(c))

  //memory, jvm, nodes, cpu etc
  val clusterStats = P("cluster stats").map(
    _ => GetClusterStatsInstruction())

  val catNodes = P("cat nodes").map(
    _ =>
      CatNodesInstruction())

  val catAllocation = P("cat allocation").map(
    _ =>
      CatAllocationInstruction())
  val catMaster = P("cat master").map(
    _ =>
      CatMasterInstruction())

  val catIndices = P("cat indices").map(
    _ => CatIndicesInstruction())

  val catShards = P("cat shards").map(
    _ =>
      CatShardsInstruction())

  val catCount = P("cat count").map(
    _ => CatCountInstruction())
  val catRecovery = P("cat recovery").map(
    _ =>
      CatRecoveryInstruction())
  val catPendingTasks = P("cat pending_tasks")
    .map(_ =>
      CatPendingInstruction())

  //indices, aliases, restore, snapshots, routing nodes etc
  val clusterState = P("cluster state").map(
    _ =>
      GetClusterStateInstruction())

  val indicesStats = P("indices stats").map(
    s =>
      IndicesStatsInstruction())
  val nodeStats = P("node stats").map(
    s =>
      NodeStatsInstruction())

  val clusterSettings = P("cluster settings").map(
    s =>
      ClusterSettingsInstruction())
  val nodeSettings = P("node settings").map(
    s =>
      NodeSettingsInstruction())
  val indexSettings = P(ioParser ~ "settings")
    .map(i => i.head.extract[String])
    .map(
      s =>
        IndexSettingsInstruction(s))
  val pendingTasks = P("pending tasks").map(
    s =>
      PendingTasksInstruction())

  val deleteDoc =
    P("delete" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~ strOrVar)
      .map(
        c =>
          DeleteDocInstruction(c._1.extract[String], c._2.extract[String], c._3.extract[String])
      )
  val deleteIndex = P("delete" ~ "index" ~/ strOrVar).map(
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
  //
  //  val search: P[interceptFunction.Instruction] = P(
  //    "search" ~ "in" ~/ strOrVar ~ ("/" ~ strOrVar).? ~ (matchQuery | joinSearch).?)
  //    .map(c =>
  //      c._2 match {
  //        case None =>
  //          c._3 match {
  //            case None =>
  //              interceptFunction.Instruction("query",
  //                interceptFunction.query,
  //                Seq(c._1))
  //            case Some(t) =>
  //              interceptFunction.Instruction(t.name, t.f, Seq(c._1) ++ t.params)
  //          }
  //        case Some(f) =>
  //          c._3 match {
  //            case None =>
  //              interceptFunction.Instruction("query",
  //                interceptFunction.query,
  //                Seq(c._1, f))
  //            case Some(t) =>
  //              interceptFunction.Instruction(t.name,
  //                t.f,
  //                Seq(c._1, f) ++ t.params)
  //          }
  //      })
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
  val extractJSON = P("\\\\" ~ strOrVar).map(c => ("extract", c.value))
  //val beauty = P("beauty").map(c => ("beauty", beautyJson))

  val instrument: P[Instruction2] = P(
    (
      healthP | shutdown | clusterStats | indicesStats | nodeStats | pendingTasks
        | clusterSettings | nodeSettings | indexSettings | clusterState
        | catNodes | catAllocation | catIndices | catMaster | catShards | catCount | catPendingTasks | catRecovery
        | count
      ) ~ extractJSON.?
  ).map(t => {
    t._1
  })

}

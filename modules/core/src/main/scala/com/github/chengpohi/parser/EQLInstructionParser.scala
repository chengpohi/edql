package com.github.chengpohi.parser

import fastparse.NoWhitespace._
import fastparse._

trait EQLInstructionParser extends JsonParser with InterceptFunction {
  def helpP[_: P] = P(alphaChars.rep(1).! ~ "?")
    .map(s => {
      HelpInstruction(Seq(s))
    })

  def healthP[_: P] = P("health").map(
    _ => HealthInstruction())


  def shutdown[_: P] = P("shutdown").map(
    _ =>
      ShutdownInstruction())

  def count[_: P] = P("count" ~/ ioParser)
    .map(i => i.head.extract[String])
    .map(c => CountInstruction(c))

  def comment[_: P] = P("#" ~ noNewlineChars.rep(0).! ~/ newline.?).map(
    _ => CommentInstruction())

  def hostBind[_: P] = P(space ~ "HOST" ~ space ~ actionPath).map(
    c => EndpointBindInstruction(c.extract[String]))

  def timeoutBind[_: P] = P(space ~ "Timeout" ~ space ~ number ~ space).map(
    c => TimeoutInstruction(c.extract[Int]))

  def authorizationBind[_: P] = P(space ~ "Authorization" ~ space ~ (actionPath | quoteString)).map(
    c => {
      AuthorizationBindInstruction(c.extract[String])
    })

  def postAction[_: P] = P(space ~ "POST" ~ space ~ actionPath ~/ newlineOrComment ~/ jsonExpr.rep.?).map(
    c => PostActionInstruction(c._1.extract[String], c._2))

  def getAction[_: P] = P(space ~ "GET" ~ space ~ actionPath ~/ newlineOrComment ~/ jsonExpr.?).map(
    c => GetActionInstruction(c._1.extract[String], c._2))

  def deleteAction[_: P] = P(space ~ "DELETE" ~ space ~ actionPath ~/ newlineOrComment ~/ jsonExpr.?).map(
    c => DeleteActionInstruction(c._1.extract[String], c._2))

  def putAction[_: P] = P(space ~ "PUT" ~ space ~ actionPath ~/ newlineOrComment ~/ jsonExpr.?).map(
    c => PutActionInstruction(c._1.extract[String], c._2))

  def headAction[_: P] = P(space ~ "HEAD" ~ space ~ actionPath ~/ newlineOrComment ~/ jsonExpr.?).map(
    c => HeadActionInstruction(c._1.extract[String], c._2))

  def variableAction[_: P] = P(space ~ "local" ~ space ~ variableName ~ space ~/ "=" ~ space ~/ jsonExpr).map(
    c => VariableInstruction(c._1, c._2))

  def functionExpr[_: P] = P("function" ~ space ~ variableName ~ space ~/ "(" ~
    space ~ variableName.rep(sep = space ~ "," ~ space) ~ space ~ ")" ~/
    space ~ "{" ~/ space ~ inses ~ space ~ "}")
    .map(c => FunctionInstruction(c._1, c._2, c._3))

  def functionInvokeExpr[_: P] = P(space ~ variableName ~ space ~ "(" ~ jsonExpr.rep(sep = space ~ "," ~ space) ~ ")" ~ newlineOrComment.?)
    .map(c => FunctionInvokeInstruction(c._1, c._2))

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
    _ =>
      ClusterSettingsInstruction())

  def indicesStats[_: P] = P("indices" ~ space ~ "stats" ~/ newline.?).map(
    _ =>
      IndicesStatsInstruction())

  def nodeStats[_: P] = P("node" ~ space ~ "stats" ~/ newline.?).map(
    _ =>
      NodeStatsInstruction())


  def nodeSettings[_: P] = P("node" ~ space ~ "settings" ~/ newline.?).map(
    _ =>
      NodeSettingsInstruction())


  def pendingTasks[_: P] = P("pending" ~ space ~ "tasks" ~/ newline.?).map(
    _ =>
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

  def search[_: P] = P(
    "search" ~ "in" ~/ strOrVar)
    .map(c => {
      val indexName = c.extract[String]
      QueryInstruction(indexName, None, Map())
    }
    )

  def extractJSON[_: P]: P[(String, String)] = P("\\\\" ~ strOrVar).map(c => ("extract", c.value))

  //val beauty = P("beauty").map(c => ("beauty", beautyJson))

  def instrument[_: P]: P[Seq[Instruction2]] = P(
    inses ~ End
  )

  private def inses[_: P]: P[Seq[Instruction2]] = {
    (
      comment |
        healthP | shutdown | clusterStats | indicesStats | nodeStats | pendingTasks
        | search
        | clusterSettings | nodeSettings | indexSettings | clusterState
        | catNodes | catAllocation | catIndices | catMaster | catShards | catCount | catPendingTasks | catRecovery
        | hostBind | timeoutBind | authorizationBind | postAction | getAction | deleteAction | putAction | headAction | variableAction | functionExpr | functionInvokeExpr
        | count
      ).rep(0)
  }
}

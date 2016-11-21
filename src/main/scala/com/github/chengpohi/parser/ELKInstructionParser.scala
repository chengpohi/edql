package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection
import com.github.chengpohi.collection.JsonCollection.{Str, Val}
import fastparse.noApi._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticservice
  * Created by chengpohi on 1/18/16.
  */
class ELKInstructionParser(elkCommand: ELKCommand, parserUtils: ParserUtils) extends CollectionParser {

  import WhitespaceApi._
  import elkCommand._

  val help = P(alphaChars.rep(1).! ~ "?")
    .map(JsonCollection.Str)
    .map(s => ("help", Some(parserUtils.help), Seq(s)))
  val health = P("health").map(s => ("health", Some(elkCommand.health), Seq(Str(""))))
  val shutdown = P("shutdown").map(s => ("shutdown", Some(elkCommand.shutdown), Seq(Str(""))))
  val count = P("count" ~/ ioParser).map(c => ("count", Some(elkCommand.count), c))
  //memory, jvm, nodes, cpu etc
  val clusterStats = P("cluster stats").map(s => ("clusterStats", Some(elkCommand.clusterStats), Seq()))
  //indices, aliases, restore, snapshots, routing nodes etc
  val clusterState = P("cluster state").map(s => ("clusterState", Some(elkCommand.getIndices), Seq()))
  val indicesStats = P("indices stats").map(s => ("indiciesStats", Some(elkCommand.indicesStats), Seq()))
  val nodeStats = P("node stats").map(s => ("nodeStats", Some(elkCommand.nodeStats), Seq()))
  val clusterSettings = P("cluster settings").map(s => ("clusterSettings", Some(elkCommand.clusterSettings), Seq()))
  val nodeSettings = P("node settings").map(s => ("nodeSettings", Some(elkCommand.nodeSettings), Seq()))
  val indexSettings = P(ioParser ~ "settings").map(s => ("nodeSettings", Some(elkCommand.indexSettings), s))
  val pendingTasks = P("pending tasks").map(s => ("pendingTasks", Some(elkCommand.pendingTasks), Seq()))
  val waitForStatus = P("wait for status" ~ ioParser).map(s => ("pendingTasks", Some(elkCommand.waitForStatus), s))
  val deleteDoc = P("delete" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~ strOrVar)
    .map(c => ("delete", Some(elkCommand.delete), Seq(c._1, c._2, c._3)))
  val deleteIndex = P("delete" ~ "index" ~/ strOrVar).map(c => ("delete", Some(elkCommand.delete), Seq(c)))

  val termQuery = P("term" ~ "query" ~/ ioParser).map(c => ("termQuery", Some(elkCommand.query), c))
  val joinSearch = P("join" ~ strOrVar ~ "/"  ~ strOrVar ~ "by" ~ strOrVar)
    .map(c => ("joinQuery", Some(elkCommand.query), Seq(c._1, c._2, c._3)))
  val matchQuery = P("match" ~/ jsonExpr)
    .map(c => ("matchQuery", Some(elkCommand.matchQuery), Seq(c)))
  val search = P("search" ~ "in" ~/ strOrVar ~ ("/" ~ strOrVar ~ (matchQuery | joinSearch).?).?)
    .map(c => c._2 match {
      case None => ("query", Some(elkCommand.query), Seq(c._1))
      case Some((f, None)) => ("query", Some(elkCommand.query), Seq(c._1, f))
      case Some((f, Some(t))) => (t._1, t._2, Seq(c._1, f) ++ t._3)
    })

  val reindex = P("reindex" ~ "into" ~ strOrVar ~ "/" ~/ strOrVar ~ "from" ~/ strOrVar ~ "fields" ~/ jsonExpr)
    .map(c => ("reindex", Some(elkCommand.reindex), Seq(c._1, c._2, c._3, c._4)))
  val index = P("index" ~ "into" ~/ strOrVar ~ "/" ~ strOrVar ~/ "fields" ~ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(c => ("index", Some(elkCommand.index), Seq(c._1, c._2, c._3) ++ c._4.toSeq))
  val bulkIndex = P("bulk index" ~/ ioParser).map(c => ("bulkIndex", Some(elkCommand.bulkIndex), c))
  val updateMapping = P("update mapping" ~/ ioParser).map(c => ("umapping", Some(elkCommand.updateMapping), c))
  val update = P("update" ~ "on" ~/ strOrVar ~ "/" ~ strOrVar ~ "fields" ~/ jsonExpr ~ ("id" ~ strOrVar).?)
    .map(c => ("update", Some(elkCommand.update), Seq(c._1, c._2, c._3) ++ c._4.toSeq))
  val createIndex = P("create" ~ "index" ~/ strOrVar).map(c => ("createIndex", Some(elkCommand.createIndex), Seq(c)))
  val getMapping = P(strOrVar ~ "mapping").map(c => ("getMapping", Some(elkCommand.getMapping), Seq(c)))
  val analysis = P("analysis" ~/ strOrVar ~/ "by" ~/ strOrVar).map(c => ("analysis", Some(elkCommand.analysis), Seq(c._1, c._2)))
  val createAnalyzer = P("create analyzer" ~/ ioParser).map(c => ("createAnalyzer", Some(elkCommand.createAnalyzer), c))
  val getDocById = P("get" ~ "from" ~/ strOrVar ~ "/" ~/ strOrVar ~ "id" ~/ strOrVar)
    .map(c => ("getDocById", Some(elkCommand.getDocById), Seq(c._1, c._2, c._3)))
  val mapping = P("mapping" ~/ ioParser).map(c => ("mapping", Some(elkCommand.mapping), c))
  val avgAggs = P("avg" ~/ strOrVar).map(c => ("avgAggs", elkCommand.aggsCount, Seq(c)))
  val termsAggs = P("term" ~/ strOrVar).map(c => ("termAggs", elkCommand.aggsTerm, Seq(c)))
  val histAggs = P("hist" ~/ strOrVar ~ "interval" ~/ strOrVar ~/ "field" ~ strOrVar).map(c => ("histAggs", elkCommand.histAggs, Seq(c._1, c._2, c._3)))
  val aggs = P("aggs in" ~/ strOrVar ~ "/" ~ strOrVar ~/ (avgAggs | termsAggs | histAggs))
    .map(c => (c._3._1, Some(c._3._2), Seq(c._1, c._2) ++ c._3._3))
  val alias = P("alias" ~/ ioParser).map(c => ("alias", Some(elkCommand.alias), c))
  val createRepository = P("create repository" ~/ ioParser).map(c => ("createRepository", Some(elkCommand.createRepository), c))
  val createSnapshot = P("create snapshot " ~/ ioParser).map(c => ("createSnapshot", Some(elkCommand.createSnapshot), c))
  val deleteSnapshot = P("delete snapshot " ~/ ioParser).map(c => ("deleteSnapshot", Some(elkCommand.deleteSnapshot), c))
  val getSnapshot = P("get snapshot " ~/ ioParser).map(c => ("getSnapshot", Some(elkCommand.getSnapshot), c))
  val restoreSnapshot = P("restore snapshot " ~/ ioParser).map(c => ("restoreSnapshot", Some(elkCommand.restoreSnapshot), c))
  val closeIndex = P("close index" ~/ ioParser).map(c => ("closeIndex", Some(elkCommand.closeIndex), c))
  val openIndex = P("open index" ~/ ioParser).map(c => ("openIndex", Some(elkCommand.openIndex), c))
  val dumpIndex = P("dump index" ~/ strOrVar ~/ ">" ~/ strChars.rep(1).!)
    .map(c => ("dumpIndex", Some(elkCommand.dumpIndex), Seq(c._1, JsonCollection.Str(c._2))))
  val extractJSON = P("\\\\" ~ strOrVar).map(c => ("extract", findJSONElements(c.value)))
  val beauty = P("beauty").map(c => ("beauty", beautyJson))

  val instrument = P((help | health | shutdown | clusterStats | indicesStats | nodeStats | pendingTasks | waitForStatus
    | clusterSettings | nodeSettings | indexSettings | clusterState
    | restoreSnapshot | deleteSnapshot | createSnapshot | getSnapshot | createRepository
    | deleteDoc | deleteIndex
    | search | termQuery | getDocById
    | reindex | index | bulkIndex | createIndex | closeIndex | openIndex | dumpIndex
    | updateMapping | update | analysis | aggs | createAnalyzer
    | getMapping | mapping
    | alias | count)
    ~ (extractJSON).?).map(i => i._4 match {
    case Some((name, extractFunction)) if i._2.isDefined => {
      val f: Seq[Val] => Future[String] = i._2.get
      val fComponent = (s: Seq[Val]) => {
        f(s).map(t => extractFunction(t))
      }
      ELK.Instrument(i._1, Some(fComponent), i._3)
    }
    case _ => ELK.Instrument((i._1, i._2, i._3))
  })
}

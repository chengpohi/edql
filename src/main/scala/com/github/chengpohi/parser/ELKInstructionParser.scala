package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection
import com.github.chengpohi.collection.JsonCollection.{Str, Val}
import fastparse.noApi._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
  val delete = P("delete" ~/ ioParser).map(c => ("delete", Some(elkCommand.delete), c))
  val termQuery = P("term" ~ "query" ~/ ioParser).map(c => ("termQuery", Some(elkCommand.query), c))
  val query = P("query" ~/ ioParser ~ joinQuery.?).map(c => ("query", Some(elkCommand.query), c._1 ++ c._2.getOrElse(Seq())))
  val joinQuery = P("join" ~ ioParser ~ "by" ~ ioParser).map(c => c._1 ++ c._2)
  val reindex = P("reindex" ~/ ioParser).map(c => ("reindex", Some(elkCommand.reindex), c))
  val index = P("index" ~/ ioParser ~ ("id" ~ ioParser).?)
    .map(c => ("index", Some(elkCommand.index), c._1 ++ c._2.getOrElse(Seq())))
  val bulkIndex = P("bulk index" ~/ ioParser).map(c => ("bulkIndex", Some(elkCommand.bulkIndex), c))
  val updateMapping = P("update mapping" ~/ ioParser).map(c => ("umapping", Some(elkCommand.updateMapping), c))
  val update = P("update" ~/ ioParser ~ ("id" ~ ioParser).?)
    .map(c => ("update", Some(elkCommand.update), c._1 ++ c._2.getOrElse(Seq())))
  val createIndex = P("create index" ~/ strOrVar).map(c => ("createIndex", Some(elkCommand.createIndex), Seq(c)))
  val getMapping = P(strOrVar ~ "mapping").map(c => ("getMapping", Some(elkCommand.getMapping), Seq(c)))
  val analysis = P("analysis" ~/ strOrVar.rep(2)).map(c => ("analysis", Some(elkCommand.analysis), c))
  val createAnalyzer = P("create analyzer" ~/ ioParser).map(c => ("createAnalyzer", Some(elkCommand.createAnalyzer), c))
  val getDocById = P("get" ~/ strOrVar.rep(3)).map(c => ("getDocById", Some(elkCommand.getDocById), c))
  val mapping = P("mapping" ~/ ioParser).map(c => ("mapping", Some(elkCommand.mapping), c))
  val aggsCount = P("aggs count" ~/ ioParser).map(c => ("aggsCount", Some(elkCommand.aggsCount), c))
  val alias = P("alias" ~/ ioParser).map(c => ("alias", Some(elkCommand.alias), c))
  val createRepository = P("create repository" ~/ ioParser).map(c => ("createRepository", Some(elkCommand.createRepository), c))
  val createSnapshot = P("create snapshot " ~/ ioParser).map(c => ("createSnapshot", Some(elkCommand.createSnapshot), c))
  val deleteSnapshot = P("delete snapshot " ~/ ioParser).map(c => ("deleteSnapshot", Some(elkCommand.deleteSnapshot), c))
  val getSnapshot = P("get snapshot " ~/ ioParser).map(c => ("getSnapshot", Some(elkCommand.getSnapshot), c))
  val restoreSnapshot = P("restore snapshot " ~/ ioParser).map(c => ("restoreSnapshot", Some(elkCommand.restoreSnapshot), c))
  val closeIndex = P("close index" ~/ ioParser).map(c => ("closeIndex", Some(elkCommand.closeIndex), c))
  val openIndex = P("open index" ~/ ioParser).map(c => ("openIndex", Some(elkCommand.openIndex), c))
  val extractJSON = P("\\\\" ~ strOrVar).map(c => ("extract", findJSONElements(c.value)))
  val beauty = P("beauty").map(c => ("beauty", beautyJson))

  val instrument = P((help | health | clusterStats | indicesStats | nodeStats | pendingTasks | waitForStatus
      | clusterSettings | nodeSettings | indexSettings | clusterState
      | restoreSnapshot | deleteSnapshot | createSnapshot | getSnapshot | createRepository
      | query | termQuery | getDocById
      | reindex | index | bulkIndex | createIndex | closeIndex | openIndex
      | updateMapping | update | analysis | aggsCount | createAnalyzer
      | getMapping | mapping
      | delete | alias | count)
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

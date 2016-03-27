package com.github.chengpohi.parser

import com.github.chengpohi.collection.JsonCollection
import com.github.chengpohi.collection.JsonCollection.{Str, Val}

/**
 * elasticservice
 * Created by chengpohi on 1/18/16.
 */
class ELKInstrumentParser extends CollectionParser {
  val elkCommand = new ELKCommand
  val parserUtils = new ParserUtils

  import elkCommand._
  import fastparse.all._

  val help = P(space ~ strChars.rep(1).! ~ space ~ "?")
    .map(JsonCollection.Str)
    .map(s => ("help", Some(parserUtils.help _), Seq(s)))
  val health = P("health").map(s => ("health", Some(h), Seq(Str(""))))
  val count = P("count" ~ space ~/ ioParser).map(c => ("count", Some(cn), c))
  //memory, jvm, nodes, cpu etc
  val clusterStats = P(space ~ "cluster stats" ~ space).map(s => ("clusterStats", Some(cst), Seq()))
  //indices, aliases, restore, snapshots, routing_nodes etc
  val clusterState = P(space ~ "cluster state" ~ space).map(s => ("clusterState", Some(clst), Seq()))
  val indicesStats = P(space ~ "indices stats" ~ space).map(s => ("indiciesStats", Some(ist), Seq()))
  val nodeStats = P(space ~ "node stats" ~ space).map(s => ("nodeStats", Some(nst), Seq()))
  val clusterSettings = P(space ~ "cluster settings" ~ space).map(s => ("clusterSettings", Some(csts), Seq()))
  val nodeSettings = P(space ~ "node settings" ~ space).map(s => ("nodeSettings", Some(nsts), Seq()))
  val indexSettings = P(ioParser ~ space ~ "settings" ~ space).map(s => ("nodeSettings", Some(insts), s))
  val pendingTasks = P("pending tasks").map(s => ("pendingTasks", Some(pt), Seq()))
  val waitForStatus = P("wait for status" ~ space ~ ioParser).map(s => ("pendingTasks", Some(ws), s))
  val delete = P("delete" ~ space ~/ ioParser).map(c => ("delete", Some(d), c))
  val termQuery = P("term" ~ space ~ "query" ~ space ~/ ioParser).map(c => ("termQuery", Some(q), c))
  val query = P("query" ~ space ~/ ioParser).map(c => ("query", Some(q), c))
  val reindex = P("reindex" ~ space ~/ ioParser ~/ space).map(c => ("reindex", Some(r), c))
  val index = P("index" ~ space ~/ ioParser ~ space).map(c => ("index", Some(i), c))
  val bulkIndex = P("bulk index" ~ space ~/ ioParser ~ space).map(c => ("bulkIndex", Some(bi), c))
  val update = P("update" ~ space ~/ ioParser ~ space).map(c => ("update", Some(u), c))
  val createIndex = P("create index" ~ space ~/ strOrVar).map(c => ("createIndex", Some(ci), Seq(c)))
  val getMapping = P(space ~ strOrVar ~ space ~ "mapping").map(c => ("getMapping", Some(gm), Seq(c)))
  val analysis = P("analysis" ~ space ~/ strOrVar.rep(2, sep = " ")).map(c => ("analysis", Some(a), c))
  val getDocById = P("get" ~ space ~/ strOrVar.rep(3, sep = " ")).map(c => ("getDocById", Some(gd), c))
  val mapping = P("mapping" ~ space ~/ ioParser).map(c => ("mapping", Some(m), c))
  val aggsCount = P("aggs count" ~ space ~/ ioParser).map(c => ("aggsCount", Some(ac), c))
  val alias = P("alias" ~ space ~/ ioParser).map(c => ("alias", Some(al), c))
  val createRepository = P("create repository" ~ space ~/ ioParser).map(c => ("createRepository", Some(cr), c))
  val createSnapshot = P("create snapshot " ~ space ~/ ioParser).map(c => ("createSnapshot", Some(cs), c))
  val deleteSnapshot = P("delete snapshot " ~ space ~/ ioParser).map(c => ("createSnapshot", Some(ds), c))
  val getSnapshot = P("get snapshot " ~ space ~/ ioParser).map(c => ("getSnapshot", Some(gs), c))
  val restoreSnapshot = P("restore snapshot " ~ space ~/ ioParser).map(c => ("restoreSnapshot", Some(rs), c))
  val closeIndex = P("close index" ~ space ~/ ioParser).map(c => ("closeIndex", Some(clI), c))
  val openIndex = P("open index" ~ space ~/ ioParser).map(c => ("openIndex", Some(oi), c))
  val extractJSON = P(space ~ "\\\\" ~ space ~ strOrVar ~ space).map(c => ("extract", findJSONElements(c.value)))
  val beauty = P(space ~ "beauty" ~ space).map(c => ("beauty", beautyJson))

  val instrument = P(space ~
    (help | health | clusterStats | indicesStats | nodeStats | pendingTasks | waitForStatus
      | clusterSettings | nodeSettings | indexSettings | clusterState
      | restoreSnapshot | deleteSnapshot  | createSnapshot | getSnapshot | createRepository
      | query | termQuery | getDocById
      | reindex | index | bulkIndex | createIndex | closeIndex | openIndex
      | update | analysis | aggsCount
      | getMapping | mapping
      | delete | alias | count)
    ~ space ~ (extractJSON).? ~ space).map(i => i._4 match {
    case Some((name, extractFunction)) if i._2.isDefined => {
      val f: Seq[Val] => String = i._2.get
      val fComponent = (f andThen extractFunction)(_)
      ELK.Instrument(i._1, Some(fComponent), i._3)
    }
    case _ => ELK.Instrument((i._1, i._2, i._3))
  })
}

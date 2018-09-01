package com.github.chengpohi.parser

import com.github.chengpohi.api.EQLClient
import com.github.chengpohi.collection.JsonCollection._
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future


class InterceptFunction(val elasticCommand: EQLClient) {
  val MAX_NUMBER: Int = 500

  import elasticCommand._

  def getMapping: Seq[Val] => GetMappingDefinition = {
    case Seq(indexName) => {
      get mapping indexName
    }
  }

  def createIndex: Seq[Val] => CreateIndexDefinition = {
    case Seq(indexName) => create index indexName
  }

  def getClusterSettings: Seq[Val] => ClusterStateRequestDefinition = _ => {
    cluster state
  }
  def getClusterState: Seq[Val] => ClusterStateRequestDefinition = _ => {
    cluster state
  }

  def clusterStats: Seq[Val] => ClusterStatsRequestDefinition = _ => {
    cluster stats
  }

  def catNodes: Seq[Val] => CatNodesDefinition = _ => {
    cat nodes
  }

  def catAllocation: Seq[Val] => CatAllocationDefinition = _ => {
    cat allocation
  }
  def catMaster: Seq[Val] => CatMasterDefinition = _ => {
    cat master
  }
  def catIndices: Seq[Val] => CatIndicesDefinition = _ => {
    cat indices
  }
  def catShards: Seq[Val] => CatShardsDefinition = _ => {
    cat shards
  }

  def clusterHealth: Seq[Val] => ClusterHealthRequestDefinition = _ => {
    cluster health
  }

  def indicesStats: Seq[Val] => IndicesStatsRequestDefinition = _ => {
    indice stats NodeType.ALL flag FlagType.ALL
  }

  def nodeStats: Seq[Val] => NodeStatsRequestDefinition = _ => {
    node stats NodeType.ALL flag FlagType.ALL
  }

  def clusterSettings: Seq[Val] => ClusterStateRequestDefinition = _ => {
    cluster settings
  }

  def nodeSettings: Seq[Val] => NodeInfoRequestDefinition = _ => {
    node info
  }

  def pendingTasks: Seq[Val] => PendingClusterTasksDefinition = _ => {
    pending tasks
  }

  def indexSettings: Seq[Val] => GetSettingsRequestDefinition = {
    case Seq(indexName) => {
      get settings indexName
    }
  }

  def health: Seq[Val] => ClusterHealthRequestDefinition = _ => {
    cluster health
  }

  def shutdown: Seq[Val] => ShutDownRequestDefinition = _ => {
    ShutDownRequestDefinition()
  }

  def count: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName) =>
      search in indexName size 0
  }

  def deleteIndex: Seq[Val] => DeleteIndexRequestDefinition = {
    case Seq(indexName) => {
      delete index indexName
    }
  }

  def deleteDoc: Seq[Val] => DeleteRequestDefinition = {
    case Seq(indexName, indexType, _id) => {
      delete in indexName / indexType id _id
    }
  }

  def matchQuery: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName, indexType, queryData) => {
      search in indexName / indexType mth queryData
        .extract[Map[String, String]]
        .toList
        .head from 0 size MAX_NUMBER
    }
  }

  def query: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName, indexType) =>
      search in indexName / indexType query "*" from 0 size MAX_NUMBER
    case Seq(indexName, indexType, queryData) =>
      search in indexName / indexType must queryData
        .extract[Map[String, String]]
        .toList from 0 size MAX_NUMBER
    case Seq(indexName) =>
      search in indexName / "*" query "*" from 0 size MAX_NUMBER
  }

  def joinQuery: Seq[Val] => JoinSearchRequestDefinition = {
    case Seq(indexName, indexType, joinIndexName, joinIndexType, field) =>
      search in indexName / indexType size MAX_RETRIEVE_SIZE scroll "10m" join joinIndexName / joinIndexType by field
  }

  def bulkUpdateDoc: Seq[Val] => BulkUpdateRequestDefinition = {
    case Seq(indexName, indexType, updateFields) => {
      bulk update indexName / indexType fields updateFields
        .extract[List[(String, String)]]
    }
  }

  def updateDoc: Seq[Val] => UpdateRequestDefinition = {
    case Seq(indexName, indexType, updateFields, _id) => {
      update id _id in indexName / indexType docAsUpsert updateFields
        .extract[List[(String, String)]]
    }
  }

  def reindexIndex: Seq[Val] => ReindexRequestDefinition = {
    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
      reindex into targetIndex / sourceIndexType from sourceIndex fields fields
        .extract[List[String]]
    }
  }

  def bulkIndex: Seq[Val] => BulkIndexRequestDefinition = {
    case Seq(indexName, indexType, fields) => {
      bulk index indexName / indexType doc fields
        .extract[List[List[(String, String)]]]
    }
  }

  def createDoc: Seq[Val] => IndexRequestDefinition = {
    case Seq(indexName, indexType, fields) => {
      index into indexName / indexType fields fields
        .extract[List[(String, String)]]
    }
    case Seq(indexName, indexType, fields, _id) => {
      index into indexName / indexType fields fields
        .extract[List[(String, String)]] id _id
    }
  }

  def analysisText: Seq[Val] => AnalyzeRequestDefinition = {
    case Seq(doc, analyzer) => {
      analyze text doc in ELASTIC_SHELL_INDEX_NAME analyzer analyzer
    }
  }

  def createAnalyzer: Seq[Val] => CreateAnalyzerRequestDefinition = {
    case Seq(analyzer) => {
      val analysisSettings = Obj(("analysis", analyzer))
      create analyzer analysisSettings.toJson
    }
  }

  def mapping: Seq[Val] => CreateIndexDefinition = {
    case Seq(indexName, mapping) => {
      create index indexName mappings mapping.toJson
    }
  }

  def updateMapping: Seq[Val] => PutMappingRequestDefinition = {
    case Seq(indexName, indexType, mapping) => {
      update index indexName / indexType mapping mapping.toJson
    }
  }

  def aggsCount: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName, indexType, name) => {
      aggs in indexName / indexType avg name
    }
  }

  def aggsTerm: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName, indexType, name) => {
      aggs in indexName / indexType term name
    }
  }

  def histAggs: Seq[Val] => SearchRequestDefinition = {
    case Seq(indexName, indexType, name, _interval, _field) => {
      aggs in indexName / indexType hist name interval _interval field _field
    }
  }

  def alias: Seq[Val] => AddAliasRequestDefinition = {
    case Seq(targetIndex, sourceIndex) => {
      add alias targetIndex on sourceIndex
    }
  }

  def getDocById: Seq[Val] => GetRequestDefinition = {
    case Seq(indexName, indexType, _id) => {
      search in indexName / indexType where id equal _id
    }
  }

  def createRepository: Seq[Val] => PutRepositoryDefinition = {
    case Seq(repositoryName, repositoryType, settings) => {
      create repository repositoryName tpe repositoryType settings settings
        .extract[Map[String, String]]
    }
  }

  def createSnapshot: Seq[Val] => CreateSnapshotDefinition = {
    case Seq(snapshotName, repositoryName) => {
      create snapshot snapshotName in repositoryName
    }
  }

  def deleteSnapshot: Seq[Val] => DeleteSnapshotDefinition = {
    case Seq(snapshotName, repositoryName) => {
      delete snapshot snapshotName from repositoryName
    }
  }

  def restoreSnapshot: Seq[Val] => RestoreSnapshotRequestDefinition = {
    case Seq(snapshotName, repositoryName) => {
      restore snapshot snapshotName from repositoryName
    }
  }

  def closeIndex: Seq[Val] => CloseIndexRequestDefinition = {
    case Seq(indexName) => {
      close index indexName
    }
  }

  def openIndex: Seq[Val] => OpenIndexRequestDefinition = {
    case Seq(indexName) => {
      open index indexName
    }
  }

  def dumpIndex: Seq[Val] => DumpIndexRequestDefinition = {
    case Seq(indexName, fileName) => {
      dump index indexName store fileName
    }
  }

  def getSnapshot: Seq[Val] => GetSnapshotDefinition = {
    case Seq(snapshotName, repositoryName) => {
      get snapshot snapshotName from repositoryName
    }
    case Seq(repositoryName) => {
      get snapshot "*" from repositoryName
    }
  }

  def waitForStatus: Seq[Val] => ClusterHealthRequestDefinition = {
    case Seq(status) => {
      waiting index "*" timeout "100s" status "GREEN"
    }
  }

  def error: Seq[Val] => ParserErrorDefinition = parameters => {
    ParserErrorDefinition(parameters)
  }

  /*  def findJSONElements(c: String): String => String = {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }*/
  val instrumentations = ConfigFactory.load("instrumentations.json")

  def help: Seq[Val] => Future[String] = {
    {
      case Seq(input) =>
        Future {
          val s = input.extract[String]
          val example: String =
            instrumentations.getConfig(s.trim).getString("example")
          val description: String =
            instrumentations.getConfig(s.trim).getString("description")
          val r: Map[String, AnyRef] =
            Map(("example", example), ("description", description))
          r.json
        }
      case _ =>
        Future {
          "I have no idea for this."
        }
    }
  }

  implicit def valToString(v: Val): String = v.extract[String]

  case class Instruction(name: String,
                         f: Seq[Val] => Definition[_],
                         params: Seq[Val])

  def buildExtractDefinition(f: Seq[Val] => Definition[_],
                             path: String): Seq[Val] => ExtractDefinition = {
    val f2: Definition[_] => ExtractDefinition = ExtractDefinition(_, path)
    f andThen f2
  }
}

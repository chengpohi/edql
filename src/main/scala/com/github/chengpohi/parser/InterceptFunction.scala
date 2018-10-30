package com.github.chengpohi.parser

import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.parser.collection.JsonCollection._
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future


class InterceptFunction(val elasticCommand: EQLClient) {
  val MAX_NUMBER: Int = 500

  import elasticCommand._

  type INSTRUMENT_TYPE = Seq[Val] => elasticCommand.Definition[_]

  def getMapping: INSTRUMENT_TYPE = {
    case Seq(indexName) => {
      get mapping indexName
    }
  }

  def createIndex: INSTRUMENT_TYPE = {
    case Seq(indexName) => create index indexName
  }

  def getClusterSettings: INSTRUMENT_TYPE = _ => {
    cluster state
  }
  def getClusterState: INSTRUMENT_TYPE = _ => {
    cluster state
  }

  def clusterStats: INSTRUMENT_TYPE = _ => {
    cluster stats
  }

  def catNodes: INSTRUMENT_TYPE = _ => {
    cat nodes
  }

  def catAllocation: INSTRUMENT_TYPE = _ => {
    cat allocation
  }
  def catMaster: INSTRUMENT_TYPE = _ => {
    cat master
  }
  def catIndices: INSTRUMENT_TYPE = _ => {
    cat indices
  }
  def catShards: INSTRUMENT_TYPE = _ => {
    cat shards
  }

  def catCount: INSTRUMENT_TYPE = _ => {
    cat count
  }
  def catPendingTasks: INSTRUMENT_TYPE = _ => {
    cat pending_tasks
  }
  def catRecovery: INSTRUMENT_TYPE = _ => {
    cat recovery
  }

  def clusterHealth: INSTRUMENT_TYPE = _ => {
    cluster health
  }

  def indicesStats: INSTRUMENT_TYPE = _ => {
    indice stats NodeType.ALL flag FlagType.ALL
  }

  def nodeStats: INSTRUMENT_TYPE = _ => {
    node stats NodeType.ALL flag FlagType.ALL
  }

  def clusterSettings: INSTRUMENT_TYPE = _ => {
    cluster settings
  }

  def nodeSettings: INSTRUMENT_TYPE = _ => {
    node info
  }

  def pendingTasks: INSTRUMENT_TYPE = _ => {
    pending tasks
  }

  def indexSettings: INSTRUMENT_TYPE = {
    case Seq(indexName) => {
      get settings indexName
    }
  }

  def health: INSTRUMENT_TYPE = _ => {
    cluster health
  }

  def shutdown: INSTRUMENT_TYPE = _ => {
    ShutDownRequestDefinition()
  }

  def count: INSTRUMENT_TYPE = {
    case Seq(indexName) =>
      search in indexName size 0
  }

  def deleteIndex: INSTRUMENT_TYPE = {
    case Seq(indexName) => {
      delete index indexName
    }
  }

  def deleteDoc: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, _id) => {
      delete in indexName / indexType id _id
    }
  }

  def matchQuery: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, queryData) => {
      search in indexName / indexType mth queryData
        .extract[Map[String, String]]
        .toList
        .head from 0 size MAX_NUMBER
    }
  }

  def query: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType) =>
      search in indexName / indexType query "*" from 0 size MAX_NUMBER
    case Seq(indexName, indexType, queryData) =>
      search in indexName / indexType must queryData
        .extract[Map[String, String]]
        .toList from 0 size MAX_NUMBER
    case Seq(indexName) =>
      search in indexName / "*" query "*" from 0 size MAX_NUMBER
  }

  def joinQuery: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, joinIndexName, joinIndexType, field) =>
      search in indexName / indexType size MAX_RETRIEVE_SIZE scroll "10m" join joinIndexName / joinIndexType by field
  }

  def bulkUpdateDoc: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, updateFields) => {
      bulk update indexName / indexType fields updateFields
        .extract[List[(String, String)]]
    }
  }

  def updateDoc: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, updateFields, _id) => {
      update id _id in indexName / indexType docAsUpsert updateFields
        .extract[List[(String, String)]]
    }
  }

  def reindexIndex: INSTRUMENT_TYPE = {
    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
      reindex into targetIndex / sourceIndexType from sourceIndex fields fields
        .extract[List[String]]
    }
  }

  def bulkIndex: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, fields) => {
      bulk index indexName / indexType doc fields
        .extract[List[List[(String, String)]]]
    }
  }

  def createDoc: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, fields) => {
      index into indexName / indexType fields fields
        .extract[List[(String, String)]]
    }
    case Seq(indexName, indexType, fields, _id) => {
      index into indexName / indexType fields fields
        .extract[List[(String, String)]] id _id
    }
  }

  def analysisText: INSTRUMENT_TYPE = {
    case Seq(doc, analyzer) => {
      analyze text doc in ELASTIC_SHELL_INDEX_NAME analyzer analyzer
    }
  }

  def createAnalyzer: INSTRUMENT_TYPE = {
    case Seq(analyzer) => {
      val analysisSettings = Obj(("analysis", analyzer))
      create analyzer analysisSettings.toJson
    }
  }

  def mapping: INSTRUMENT_TYPE = {
    case Seq(indexName, mapping) => {
      create index indexName mappings mapping.toJson
    }
  }

  def updateMapping: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, mapping) => {
      update index indexName / indexType mapping mapping.toJson
    }
  }

  def aggsCount: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, name) => {
      aggs in indexName / indexType avg name
    }
  }

  def aggsTerm: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, name) => {
      aggs in indexName / indexType term name
    }
  }

  def histAggs: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, name, _interval, _field) => {
      aggs in indexName / indexType hist name interval _interval field _field
    }
  }

  def alias: INSTRUMENT_TYPE = {
    case Seq(targetIndex, sourceIndex) => {
      add alias targetIndex on sourceIndex
    }
  }

  def getDocById: INSTRUMENT_TYPE = {
    case Seq(indexName, indexType, _id) => {
      search in indexName / indexType where id equal _id
    }
  }

  def createRepository: INSTRUMENT_TYPE = {
    case Seq(repositoryName, repositoryType, settings) => {
      create repository repositoryName tpe repositoryType settings settings
        .extract[Map[String, String]]
    }
  }

  def createSnapshot: INSTRUMENT_TYPE = {
    case Seq(snapshotName, repositoryName) => {
      create snapshot snapshotName in repositoryName
    }
  }

  def deleteSnapshot: INSTRUMENT_TYPE = {
    case Seq(snapshotName, repositoryName) => {
      delete snapshot snapshotName from repositoryName
    }
  }

  def restoreSnapshot: INSTRUMENT_TYPE = {
    case Seq(snapshotName, repositoryName) => {
      restore snapshot snapshotName from repositoryName
    }
  }

  def closeIndex: INSTRUMENT_TYPE = {
    case Seq(indexName) => {
      close index indexName
    }
  }

  def openIndex: INSTRUMENT_TYPE = {
    case Seq(indexName) => {
      open index indexName
    }
  }

  def dumpIndex: INSTRUMENT_TYPE = {
    case Seq(indexName, fileName) => {
      dump index indexName store fileName
    }
  }

  def getSnapshot: INSTRUMENT_TYPE = {
    case Seq(snapshotName, repositoryName) => {
      get snapshot snapshotName from repositoryName
    }
    case Seq(repositoryName) => {
      get snapshot "*" from repositoryName
    }
  }

  def waitForStatus: INSTRUMENT_TYPE = {
    case Seq(status) => {
      waiting index "*" timeout "100s" status "GREEN"
    }
  }

  def error: INSTRUMENT_TYPE = parameters => {
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
                         f: INSTRUMENT_TYPE,
                         params: Seq[Val])

  def buildExtractDefinition(f: INSTRUMENT_TYPE,
                             path: String): INSTRUMENT_TYPE = {
    val f2: Definition[_] => ExtractDefinition = ExtractDefinition(_, path)
    f andThen f2
  }
}

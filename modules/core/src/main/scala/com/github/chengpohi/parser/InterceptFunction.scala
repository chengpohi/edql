package com.github.chengpohi.parser

import com.github.chengpohi.dsl.EQLClient
import com.github.chengpohi.dsl.eql.{Definition, ExtractDefinition, PureStringDefinition}
import com.github.chengpohi.parser.collection.JsonCollection.{Obj, Str, Val}
import com.typesafe.config.ConfigFactory

class InterceptFunction(val eql: EQLClient) {

  val MAX_NUMBER: Int = 500

  import eql._

  type INSTRUMENT_TYPE = Seq[Val] => Definition[_]


  case class GetMappingInstruction(indexName: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      get mapping indexName
    }
  }

  case class CreateIndexInstruction(indexName: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      create index indexName
    }
  }


  case class GetClusterStateInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cluster state
    }
  }

  case class GetClusterSettingsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cluster settings
    }
  }

  case class GetClusterStatsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cluster stats
    }
  }

  case class CatNodesInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat nodes
    }
  }

  case class GetAllocationInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat allocation
    }
  }

  case class CatMasterInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat master
    }
  }

  case class CatIndicesInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat indices
    }
  }

  case class CatShardsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat shards
    }
  }

  case class CatCountInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat count
    }
  }

  case class CatPendingInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat pending_tasks
    }
  }

  case class CatRecoveryInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cat recovery
    }
  }

  case class IndicesStatsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      indice stats NodeType.ALL flag FlagType.ALL
    }
  }


  case class NodeStatsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      node stats NodeType.ALL flag FlagType.ALL
    }
  }


  case class ClusterSettingsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      cluster settings
    }
  }


  case class NodeSettingsInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      node info
    }
  }

  case class PendingTasksInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      pending tasks
    }
  }

  case class IndexSettingsInstruction(indexName: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      get settings indexName
    }
  }


  case class ShutdownInstruction(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      ShutDownRequestDefinition()
    }
  }

  case class CountInstruction(indexName: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      get settings indexName
    }
  }


  case class DeleteIndexInstruction(indexName: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      delete index indexName
    }
  }

  case class DeleteDocInstruction(indexName: String, indexType: String, _id: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      delete in indexName / indexType id _id
    }
  }

  case class MatchQueryInstruction(indexName: String, indexType: Option[String], queryData: Map[String, String])(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      search in indexName / indexType must queryData from 0 size MAX_NUMBER
    }
  }

  case class QueryInstruction(indexName: String, indexType: Option[String],
                              queryData: Map[String, String])(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: SearchRequestDefinition = {
      search in indexName / indexType must queryData from 0 size MAX_NUMBER
    }
  }

  case class JoinQueryInstruction(indexName: String,
                                  indexType: Option[String],
                                  joinIndexName: String,
                                  joinIndexType: String,
                                  joinField: String,
                                  queryData: Map[String, String])(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      QueryInstruction(indexName, indexType, queryData).execute scroll "10m" join (joinIndexName / joinIndexType) by joinField
    }
  }

  case class BulkUpdateInstruction(indexName: String,
                                   indexType: Option[String],
                                   updateFields: Map[String, String])(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      bulk update indexName / indexType fields updateFields
    }
  }


  case class UpdateDocInstruction(indexName: String, indexType: Option[String],
                              updateFields: Map[String, String], _id: String)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      update id _id in indexName / indexType docAsUpsert updateFields
    }
  }

  case class ReIndexInstruction(sourceIndex: String,)(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "GetMapping"

    override def execute: Definition[_] = {
      search in indexName / indexType must queryData from 0 size MAX_NUMBER
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
      dump index indexName into fileName
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

  implicit def valToString(v: Val): String = v.extract[String]

  case class Instruction(name: String, f: INSTRUMENT_TYPE, params: Seq[Val])

  trait Instruction2 {
    val eql: EQLClient

    def name: String

    def execute: Definition[_]
  }

  lazy val instrumentations = ConfigFactory.load("instrumentations.json")

  case class HelpInstruction(params: Seq[Str])(implicit val eql: EQLClient) extends Instruction2 {
    override def name = "help"

    def execute: PureStringDefinition = params match {
      case Seq(i) =>
        val example: String =
          instrumentations.getConfig(i.extract[String]).getString("example")
        val description: String =
          instrumentations.getConfig(i.extract[String]).getString("description")
        val r: Map[String, AnyRef] =
          Map(("example", example), ("description", description))
        PureStringDefinition(r.json)
      case _ =>
        PureStringDefinition("I have no idea for this.")
    }
  }

  case class HealthInstruction()(implicit val eql: EQLClient) extends Instruction2 {
    override def name: String = "health"

    def execute: ClusterHealthRequestDefinition = {
      cluster health
    }
  }

  def buildExtractDefinition(f: INSTRUMENT_TYPE,
                             path: String): INSTRUMENT_TYPE = {
    val f2: Definition[_] => ExtractDefinition = ExtractDefinition(_, path)
    f andThen f2
  }
}

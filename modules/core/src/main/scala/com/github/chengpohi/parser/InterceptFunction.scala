package com.github.chengpohi.parser

import com.github.chengpohi.context.EQLContext
import com.github.chengpohi.dsl.eql.{Definition, ErrorHealthRequestDefinition, PureStringDefinition}
import com.github.chengpohi.parser.collection.JsonCollection
import com.jayway.jsonpath.JsonPath
import com.typesafe.config.ConfigFactory

import java.nio.file.{Files, Paths}
import java.util.stream.Collectors

trait InterceptFunction {
  val MAX_NUMBER: Int = 500

  case class GetMappingInstruction(indexName: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      get mapping indexName
    }
  }

  case class CreateIndexInstruction(indexName: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      create index indexName
    }
  }


  case class GetClusterStateInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cluster state
    }
  }

  case class GetClusterSettingsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cluster settings
    }
  }

  case class GetClusterStatsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cluster stats
    }
  }

  case class CatNodesInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat nodes
    }
  }

  case class GetAllocationInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat allocation
    }
  }

  case class CatMasterInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat master
    }
  }

  case class CatIndicesInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat indices
    }
  }

  case class CatShardsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat shards
    }
  }

  case class CatCountInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat count
    }
  }

  case class CatAllocationInstruction() extends Instruction2 {
    override def name: String = "Allocation"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat allocation
    }
  }

  case class CatPendingInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat pending_tasks
    }
  }

  case class CatRecoveryInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cat recovery
    }
  }

  case class IndicesStatsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      indice stats NodeType.ALL flag FlagType.ALL
    }
  }


  case class NodeStatsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      node stats NodeType.ALL flag FlagType.ALL
    }
  }


  case class ClusterSettingsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cluster settings
    }
  }


  case class NodeSettingsInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      node info
    }
  }

  case class PendingTasksInstruction() extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      pending tasks
    }
  }

  case class IndexSettingsInstruction(indexName: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      get settings indexName
    }
  }


  case class ShutdownInstruction() extends Instruction2 {
    override def name: String = "ShutDown"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      ShutDownRequestDefinition()
    }
  }

  case class CountInstruction(indexName: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      get settings indexName
    }
  }


  case class DeleteIndexInstruction(indexName: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      delete index indexName
    }
  }

  case class DeleteDocInstruction(indexName: String, indexType: String, _id: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      delete in indexName / indexType id _id
    }
  }

  case class MatchQueryInstruction(indexName: String, indexType: Option[String], queryData: Map[String, String]) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      search in indexName / indexType must queryData from 0 size MAX_NUMBER
    }
  }

  case class QueryInstruction(indexName: String, indexType: Option[String],
                              queryData: Map[String, String]) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      search in indexName / indexType must queryData from 0 size MAX_NUMBER
    }
  }

  case class BulkUpdateInstruction(indexName: String,
                                   indexType: Option[String],
                                   updateFields: Map[String, String]) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      bulk update indexName / indexType fields updateFields
    }
  }


  case class UpdateDocInstruction(indexName: String, indexType: Option[String],
                                  updateFields: Map[String, String], _id: String) extends Instruction2 {
    override def name: String = "GetMapping"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      update id _id in indexName / indexType docAsUpsert updateFields
    }
  }


  //  def reindexIndex: INSTRUMENT_TYPE = {
  //    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
  //      reindex into targetIndex / sourceIndexType from sourceIndex fields fields
  //        .extract[List[String]]
  //    }
  //  }
  //
  //  def bulkIndex: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, fields) => {
  //      bulk index indexName / indexType doc fields
  //        .extract[List[List[(String, String)]]]
  //    }
  //  }
  //
  //  def createDoc: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, fields) => {
  //      index into indexName / indexType fields fields
  //        .extract[List[(String, String)]]
  //    }
  //    case Seq(indexName, indexType, fields, _id) => {
  //      index into indexName / indexType fields fields
  //        .extract[List[(String, String)]] id _id
  //    }
  //  }
  //
  //  def analysisText: INSTRUMENT_TYPE = {
  //    case Seq(doc, analyzer) => {
  //      analyze text doc in ELASTIC_SHELL_INDEX_NAME analyzer analyzer
  //    }
  //  }
  //
  //  def createAnalyzer: INSTRUMENT_TYPE = {
  //    case Seq(analyzer) => {
  //      val analysisSettings = Obj(("analysis", analyzer))
  //      create analyzer analysisSettings.toJson
  //    }
  //  }
  //
  //  def mapping: INSTRUMENT_TYPE = {
  //    case Seq(indexName, mapping) => {
  //      create index indexName mappings mapping.toJson
  //    }
  //  }
  //
  //  def updateMapping: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, mapping) => {
  //      update index indexName / indexType mapping mapping.toJson
  //    }
  //  }
  //
  //  def aggsCount: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, name) => {
  //      aggs in indexName / indexType avg name
  //    }
  //  }
  //
  //  def aggsTerm: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, name) => {
  //      aggs in indexName / indexType term name
  //    }
  //  }
  //
  //  def histAggs: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, name, _interval, _field) => {
  //      aggs in indexName / indexType hist name interval _interval field _field
  //    }
  //  }
  //
  //  def alias: INSTRUMENT_TYPE = {
  //    case Seq(targetIndex, sourceIndex) => {
  //      add alias targetIndex on sourceIndex
  //    }
  //  }
  //
  //  def getDocById: INSTRUMENT_TYPE = {
  //    case Seq(indexName, indexType, _id) => {
  //      search in indexName / indexType where id equal _id
  //    }
  //  }
  //
  //  def createRepository: INSTRUMENT_TYPE = {
  //    case Seq(repositoryName, repositoryType, settings) => {
  //      create repository repositoryName tpe repositoryType settings settings
  //        .extract[Map[String, String]]
  //    }
  //  }
  //
  //  def createSnapshot: INSTRUMENT_TYPE = {
  //    case Seq(snapshotName, repositoryName) => {
  //      create snapshot snapshotName in repositoryName
  //    }
  //  }
  //
  //  def deleteSnapshot: INSTRUMENT_TYPE = {
  //    case Seq(snapshotName, repositoryName) => {
  //      delete snapshot snapshotName from repositoryName
  //    }
  //  }
  //
  //  def restoreSnapshot: INSTRUMENT_TYPE = {
  //    case Seq(snapshotName, repositoryName) => {
  //      restore snapshot snapshotName from repositoryName
  //    }
  //  }
  //
  //  def closeIndex: INSTRUMENT_TYPE = {
  //    case Seq(indexName) => {
  //      close index indexName
  //    }
  //  }
  //
  //  def openIndex: INSTRUMENT_TYPE = {
  //    case Seq(indexName) => {
  //      open index indexName
  //    }
  //  }
  //
  //  def dumpIndex: INSTRUMENT_TYPE = {
  //    case Seq(indexName, fileName) => {
  //      dump index indexName into fileName
  //    }
  //  }
  //
  //  def getSnapshot: INSTRUMENT_TYPE = {
  //    case Seq(snapshotName, repositoryName) => {
  //      get snapshot snapshotName from repositoryName
  //    }
  //    case Seq(repositoryName) => {
  //      get snapshot "*" from repositoryName
  //    }
  //  }
  //
  //  def waitForStatus: INSTRUMENT_TYPE = {
  //    case Seq(status) => {
  //      waiting index "*" timeout "100s" status "GREEN"
  //    }
  //  }
  //
  //  def error: INSTRUMENT_TYPE = parameters => {
  //    ParserErrorDefinition(parameters)
  //  }
  //
  //  implicit def valToString(v: Val): String = v.extract[String]
  //
  //  case class Instruction(name: String, f: INSTRUMENT_TYPE, params: Seq[Val])

  trait Instruction2 {
    def name: String

    def execute(implicit eql: EQLContext): Definition[_]
  }

  lazy val instrumentations = ConfigFactory.load("instrumentations.json")

  case class HelpInstruction(params: Seq[String]) extends Instruction2 {
    override def name = "help"

    def execute(implicit eql: EQLContext): Definition[_] = {
      params match {
        case Seq(_) =>
          //          val example: String =
          //            instrumentations.getConfig(i).getString("example")
          //          val description: String =
          //            instrumentations.getConfig(i).getString("description")
          //          val r: Map[String, AnyRef] =
          //            Map(("example", example), ("description", description))
          PureStringDefinition("help")
        case _ =>
          PureStringDefinition("I have no idea for this.")
      }
    }
  }


  trait ScriptContextInstruction2 extends Instruction2

  case class CommentInstruction() extends ScriptContextInstruction2 {
    override def name = "comment"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition("")
    }
  }

  case class EndpointBindInstruction(endpoint: String) extends ScriptContextInstruction2 {
    override def name = "host"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$endpoint")
    }
  }

  case class TimeoutInstruction(timeout: Int) extends ScriptContextInstruction2 {
    override def name = "timeout"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"timeout $timeout")
    }
  }

  case class ImportInstruction(imp: String) extends ScriptContextInstruction2 {
    override def name = "import"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$imp")
    }
  }

  case class AuthorizationBindInstruction(auth: String) extends ScriptContextInstruction2 {
    override def name = "authorization"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"$auth")
    }
  }

  case class PostActionInstruction(path: String, action: Option[Seq[JsonCollection.Val]]) extends Instruction2 {
    override def name = "post"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      action.foreach(i =>
        i.foreach(j => mapRealValue(eql.variables, j)))
      val newPath = mapNewPath(eql.variables, path)

      PostActionDefinition(newPath, action.map(_.map(_.toJson)))
    }
  }


  case class DeleteActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "delete"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      action.foreach(i => mapRealValue(eql.variables, i))
      val newPath = mapNewPath(eql.variables, path)

      DeleteActionDefinition(newPath, action.map(_.toJson))
    }
  }

  case class PutActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "put"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      action.foreach(i => mapRealValue(eql.variables, i))
      val newPath = mapNewPath(eql.variables, path)

      PutActionDefinition(newPath, action.map(_.toJson))
    }
  }

  case class GetActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "get"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      action.foreach(i => mapRealValue(eql.variables, i))
      val newPath = mapNewPath(eql.variables, path)

      GetActionDefinition(newPath, action.map(_.toJson))
    }
  }

  case class HeadActionInstruction(path: String, action: Option[JsonCollection.Val]) extends Instruction2 {
    override def name = "head"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      action.foreach(i => mapRealValue(eql.variables, i))
      val newPath = mapNewPath(eql.variables, path)
      HeadActionDefinition(newPath, action.map(_.toJson))
    }

  }

  type ContextVal = Either[JsonCollection.Val, FunctionInvokeInstruction]

  case class VariableInstruction(variableName: String, value: ContextVal) extends ScriptContextInstruction2 {
    override def name = "variable"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class FunctionInstruction(funcName: String, variableNames: Seq[String], instructions: Seq[Instruction2]) extends ScriptContextInstruction2 {
    override def name = "function"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class ReturnInstruction(value: ContextVal) extends Instruction2 {
    override def name = "return"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class EchoInstruction(value: ContextVal) extends Instruction2 {
    override def name = "echo"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class FunctionInvokeInstruction(funcName: String, vals: Seq[ContextVal]) extends Instruction2 {
    override def name = "functionInvoke"

    def execute(implicit eql: EQLContext): Definition[_] = {
      PureStringDefinition(s"")
    }
  }

  case class ReadJSONInstruction(filePath: JsonCollection.Var) extends Instruction2 {
    override def name = "readJSONInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      val contextPath = eql.variables.get("CONTEXT_PATH")
      val currentDir = contextPath.map(_.asInstanceOf[JsonCollection.Str].value).map(_ + "/").getOrElse("")
      mapRealValue(eql.variables, filePath)
      val targetPath = filePath.toJson
      val content =
        Files.readAllLines(Paths.get(currentDir + targetPath.replaceAll("^\"|\"$", "")))
          .stream()
          .collect(Collectors.joining(System.lineSeparator()))

      PureStringDefinition(content)
    }
  }

  case class WriteJSONInstruction(filePath: JsonCollection.Val, data: JsonCollection.Val) extends Instruction2 {
    override def name = "writeJSONInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      val contextPath = eql.variables.get("CONTEXT_PATH")
      val currentDir = contextPath.map(_.asInstanceOf[JsonCollection.Str].value).map(_ + "/").getOrElse("")
      mapRealValue(eql.variables, filePath)
      mapRealValue(eql.variables, data)

      Files.write(
        Paths.get(currentDir + filePath.toJson.replaceAll("^\"|\"$", "")),
        data.toJson.getBytes())

      PureStringDefinition("")
    }
  }

  case class JQInstruction(data: JsonCollection.Val, path: JsonCollection.Val) extends Instruction2 {
    override def name = "jqInstruction"

    def execute(implicit eql: EQLContext): Definition[_] = {
      mapRealValue(eql.variables, data)
      val jsonO = data.toJson
      mapRealValue(eql.variables, path)
      val jsonPath = path.toJson.replaceAll("^\"|\"$", "")
      val value = JsonPath.parse(jsonO).read(jsonPath, classOf[String])
      PureStringDefinition(value)
    }
  }


  case class HealthInstruction() extends Instruction2 {

    override def name: String = "health"

    def execute(implicit eql: EQLContext): Definition[_] = {
      import eql._
      cluster health
    }
  }

  case class ErrorInstruction(error: String) extends Instruction2 {

    override def name: String = "error"

    def execute(implicit eql: EQLContext): Definition[_] = {
      ErrorHealthRequestDefinition(error)
    }
  }

  def mapRealValue(variables: scala.collection.mutable.Map[String, JsonCollection.Val], v: JsonCollection.Val): Unit = {
    if (v.vars.nonEmpty) {
      v.vars.foreach(k => {
        val realValue = variables.get(k.value)
        if (realValue.isEmpty) {
          throw new RuntimeException("could not find variable: " + k.value)
        }

        realValue.foreach(r => {
          if (r.vars.nonEmpty) {
            r.vars.foreach(t => {
              mapRealValue(variables, t)
            })
          }
        })
        k.realValue = realValue
      })
    }
  }


  private def mapNewPath(variables: scala.collection.mutable.Map[String, JsonCollection.Val], path: String) = {
    variables.foldLeft(path)((i, o) => {
      val vName = "\\$" + o._1;
      val v = o._2 match {
        case s: JsonCollection.Str => {
          s.value
        }
        case va: JsonCollection.Var => {
          mapRealValue(variables, va)
          va.realValue match {
            case Some(fa) => fa match {
              case s: JsonCollection.Str => s.value
              case j => j.toJson
            }
            case None => va.value
          }
        }
        case s => s.toJson
      }
      i.replaceAll(vName, v)
    })
  }


  def systemFunction: Map[String, FunctionInstruction] = {
    Map(
      "jq" -> FunctionInstruction("jq", Seq("data", "path"), Seq(JQInstruction(JsonCollection.Var("path"), JsonCollection.Var("data")))),
      "readJSON1" -> FunctionInstruction("readJSON", Seq("filePath"), Seq(ReadJSONInstruction(JsonCollection.Var("filePath")))),
      "writeJSON2" -> FunctionInstruction("writeJSON", Seq("filePath", "data"), Seq(WriteJSONInstruction(JsonCollection.Var("filePath"), JsonCollection.Var("data"))))
    )
  }


}


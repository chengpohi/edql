package com.github.chengpohi.parser

import com.github.chengpohi.base.ElasticCommand
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * elasticservice
 * Created by chengpohi on 1/18/16.
 */
object ELKCommand {
  val responseGenerator = new ResponseGenerator
  val TUPLE = """\(([^(),]+),([^(),]+)\)""".r


  import responseGenerator._

  val h: Seq[Any] => String = health
  val c: Seq[Any] => String = count
  val d: Seq[Any] => String = delete
  val q: Seq[Any] => String = query
  val r: Seq[Any] => String = reindex
  val i: Seq[Any] => String = index
  val bi: Seq[Any] => String = bulkIndex
  val u: Seq[Any] => String = update
  val ci: Seq[Any] => String = createIndex
  val a: Seq[Any] => String = analysis
  val gm: Seq[Any] => String = getMapping
  val gd: Seq[Any] => String = getDocById
  val m: Seq[Any] => String = mapping
  val ac: Seq[Any] => String = aggsCount

  def getMapping(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualMappingsResponse: Future[GetMappingsResponse] = ElasticCommand.getMapping(indexName)
        val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
        buildGetMappingResponse(mappings)
      }
    }
  }

  def createIndex(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) =>
        val createResponse = ElasticCommand.createIndex(indexName)
        Await.result(createResponse, Duration.Inf).isAcknowledged.toString
    }
  }

  def health(parameters: Seq[Any]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) =>
        ElasticCommand.countCommand(indexName)
    }
  }

  def delete(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case "*" => ElasticCommand.deleteIndex(indexName)
          case _ => ElasticCommand.deleteIndexType(indexName, indexType)
        }
        s"delete ${parameters.head} $indexType success"
      }
    }
  }

  def query(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case "*" => ElasticCommand.getAllDataByIndexName(indexName).toString
          case indexType: String => ElasticCommand.getAllDataByIndexTypeWithIndexName(indexName, indexType).toString
        }
      }
    }
  }

  def update(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, updateFields) => {
        val uf = updateFields.asInstanceOf[Seq[(String, String)]]
        ElasticCommand.update(indexName, indexType, uf)
      }
    }
  }

  def reindex(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
        ElasticCommand.reindex(sourceIndex, targetIndex, sourceIndexType, fields)
      }
    }
  }

  def bulkIndex(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val fs = fields.asInstanceOf[Seq[Seq[(String, String)]]]
        val bulkResponse =
          ElasticCommand.bulkIndex(indexName, indexType, fs)
        val br: BulkResponse = Await.result(bulkResponse, Duration.Inf)
        buildBulkResponse(br)
      }
    }
  }

  def index(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val fs = fields.asInstanceOf[Seq[(String, String)]]
        val indexResponse =
          ElasticCommand.indexField(indexName, indexType, fs)
        Await.result(indexResponse, Duration.Inf).getId
      }
      case Seq(indexName, indexType, fields, id) => {
        val fs = fields.asInstanceOf[Seq[(String, String)]]

        val indexResponse = ElasticCommand.indexFieldById(indexName, indexType, fs, id)
        Await.result(indexResponse, Duration.Inf).getId
      }
    }
  }

  def analysis(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(analyzer, doc) => {
        val analyzeResponse: AnalyzeResponse = Await.result(ElasticCommand.analysis(analyzer, doc), Duration.Inf)
        buildAnalyzeResponse(analyzeResponse)
      }
    }
  }

  def buildFieldType(parameters: Seq[String]) = {
    def generateType(fieldName: String, fieldType: String) = fieldType match {
      case "string" => fieldName typed StringType
      case "date" => fieldName typed DateType
    }
    parameters match {
      case Seq(fieldName, fieldSourceType) => generateType(fieldName, fieldSourceType)
      case Seq(fieldName, fieldSourceType, analyzer) => generateType(fieldName, fieldSourceType) index analyzer
    }
  }

  def mapping(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val fs: Seq[Seq[String]] = fields.asInstanceOf[Seq[Seq[String]]]
        val typeDefinitions = fs.map(f => buildFieldType(f))
        val mappings: Future[CreateIndexResponse] = ElasticCommand.mappings(indexName, indexType, typeDefinitions.toIterable)
        val result: CreateIndexResponse = Await.result(mappings, Duration.Inf)
        buildCreateIndexResponse(result)
      }
    }
  }

  def aggsCount(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, rawJson) => {
        //ElasticCommand.aggsSearch(indexName, indexType, rawJson)
        ""
      }
    }
  }


  def getDocById(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, id) => {
        val getResponse: GetResponse = Await.result(ElasticCommand.getDocById(indexName,
          indexType, id), Duration.Inf)
        buildGetResponse(getResponse)
      }
    }
  }

  def findJSONElements(c: String): String => String = {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }

  implicit def anyToObject[T](a: Any): T = a.asInstanceOf[T]
}

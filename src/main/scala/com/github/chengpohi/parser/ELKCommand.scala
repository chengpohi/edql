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
import com.github.chengpohi.collection.Js._

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

  val h: Seq[Val] => String = health
  val c: Seq[Val] => String = count
  val d: Seq[Val] => String = delete
  val q: Seq[Val] => String = query
  val r: Seq[Val] => String = reindex
  val i: Seq[Val] => String = index
  val bi: Seq[Val] => String = bulkIndex
  val u: Seq[Val] => String = update
  val ci: Seq[Val] => String = createIndex
  val a: Seq[Val] => String = analysis
  val gm: Seq[Val] => String = getMapping
  val gd: Seq[Val] => String = getDocById
  val m: Seq[Val] => String = mapping
  val ac: Seq[Val] => String = aggsCount

  def getMapping(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualMappingsResponse: Future[GetMappingsResponse] = ElasticCommand.getMapping(indexName)
        val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
        buildGetMappingResponse(mappings)
      }
    }
  }

  def createIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        val createResponse = ElasticCommand.createIndex(indexName)
        Await.result(createResponse, Duration.Inf).isAcknowledged.toString
    }
  }

  def health(parameters: Seq[Val]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        ElasticCommand.countCommand(indexName)
    }
  }

  def delete(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case Str("*") => ElasticCommand.deleteIndex(indexName)
          case _ => ElasticCommand.deleteIndexType(indexName, indexType)
        }
        s"delete ${indexName.value} ${indexType.value} success"
      }
    }
  }

  def query(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case Str("*") => ElasticCommand.getAllDataByIndexName(indexName).toString
          case _ => ElasticCommand.getAllDataByIndexTypeWithIndexName(indexName, indexType).toString
        }
      }
    }
  }

  def update(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, updateFields) => {
        val uf = updateFields.asInstanceOf[Seq[(String, String)]]
        ElasticCommand.update(indexName, indexType, uf)
      }
    }
  }

  def reindex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
        ElasticCommand.reindex(sourceIndex, targetIndex, sourceIndexType, fields)
      }
    }
  }

  def bulkIndex(parameters: Seq[Val]): String = {
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

  def index(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val indexResponse =
          ElasticCommand.indexField(indexName, indexType, fields)
        Await.result(indexResponse, Duration.Inf).getId
      }
      case Seq(indexName, indexType, fields, id) => {
        val f: List[(String, String)] = fields
        val indexResponse = ElasticCommand.indexFieldById(indexName, indexType, f, id)
        Await.result(indexResponse, Duration.Inf).getId
      }
    }
  }

  def analysis(parameters: Seq[Val]): String = {
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

  def mapping(parameters: Seq[Val]): String = {
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

  def aggsCount(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, rawJson) => {
        //ElasticCommand.aggsSearch(indexName, indexType, rawJson)
        ""
      }
    }
  }


  def getDocById(parameters: Seq[Val]): String = {
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

  //implicit def anyToObject[T](a: Any): T = a.asInstanceOf[T]
}

package com.github.chengpohi.parser

import com.github.chengpohi.base.ElasticCommand
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.get.GetResponse
import com.sksamuel.elastic4s.ElasticDsl._

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

  val h: Seq[String] => String = health
  val c: Seq[String] => String = count
  val d: Seq[String] => String = delete
  val q: Seq[String] => String = query
  val r: Seq[String] => String = reindex
  val i: Seq[String] => String = index
  val u: Seq[String] => String = update
  val ci: Seq[String] => String = createIndex
  val a: Seq[String] => String = analysis
  val gm: Seq[String] => String = getMapping
  val gd: Seq[String] => String = getDocById
  val m: Seq[String] => String = mapping

  def getMapping(parameters: Seq[String]): String = {
    val indexName = parameters.head

    val eventualMappingsResponse: Future[GetMappingsResponse] = ElasticCommand.getMapping(indexName)
    val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
    buildGetMappingResponse(mappings)
  }

  def createIndex(parameters: Seq[String]): String = {
    val createResponse = ElasticCommand.createIndex(parameters.head)
    Await.result(createResponse, Duration.Inf).isAcknowledged.toString
  }

  def health(parameters: Seq[String]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[String]): String = {
    ElasticCommand.countCommand(parameters.head)
  }

  def delete(parameters: Seq[String]): String = {
    val (indexName, indexType) = (parameters.head, parameters(1))
    indexType match {
      case "*" => ElasticCommand.deleteIndex(indexName)
      case _ => ElasticCommand.deleteIndexType(indexName, indexType)
    }

    s"delete ${parameters.head} $indexType success"
  }

  def query(parameters: Seq[String]): String = {
    val (indexName, indexType) = (parameters.head, parameters(1))
    indexType match {
      case "*" => ElasticCommand.getAllDataByIndexName(indexName).toString
      case indexType: String => ElasticCommand.getAllDataByIndexTypeWithIndexName(indexName, indexType).toString
    }
  }

  def update(parameters: Seq[String]): String = {
    val (indexName, indexType, updateFields) = (parameters.head, parameters(1), parameters(2))
    val uf: (String, String) = updateFields match {
      case TUPLE(field, value) => (field.trim, value.trim)
      case _ => null
    }
    ElasticCommand.update(indexName, indexType, uf)
  }

  def reindex(parameters: Seq[String]): String = {
    val (sourceIndex, targetIndex, sourceIndexType, fields) = (parameters.head, parameters(1), parameters(2), parameters(3))
    ElasticCommand.reindex(sourceIndex, targetIndex, sourceIndexType, fields.split(",").map(_.trim))
  }

  def index(parameters: Seq[String]): String = {
    val (indexName, indexType, fields, id) = (parameters.head, parameters(1), parameters(2), parameters(3))
    val uf: (String, String) = fields match {
      case TUPLE(field, value) => (field.trim, value.trim)
      case _ => null
    }
    id match {
      case "*" =>
        val indexResponse = ElasticCommand.indexField(indexName, indexType, uf)
        Await.result(indexResponse, Duration.Inf).getId
      case id: String =>
        val indexResponse = ElasticCommand.indexFieldById(indexName, indexType, uf, id)
        Await.result(indexResponse, Duration.Inf).getId
    }
  }

  def analysis(parameters: Seq[String]): String = {
    val (analyzer, doc) = (parameters.head, parameters(1))

    val analyzeResponse: AnalyzeResponse = Await.result(ElasticCommand.analysis(analyzer, doc), Duration.Inf)
    buildAnalyzeResponse(analyzeResponse)
  }


  def buildFieldType(key: String, value: String) = value match {
    case "string" => value typed StringType
    case "date" => value typed DateType
  }

  def mapping(parameters: Seq[String]): String = {
    val (indexName, indexType, fields) = (parameters.head, parameters(1), parameters(2))
    val typeDefinitions = TUPLE.findAllIn(fields).map {
      case TUPLE(key, value) => {
        buildFieldType(key, value)
      }
    }
    val mappings: Future[CreateIndexResponse] = ElasticCommand.mappings(indexName, indexType, typeDefinitions.toIterable)
    val result: CreateIndexResponse = Await.result(mappings, Duration.Inf)
    buildCreateIndexResponse(result)
  }

  def getDocById(parameters: Seq[String]): String = {
    val (indexName, indexType, id) = (parameters.head, parameters(1), parameters(2))
    val getResponse: GetResponse = Await.result(ElasticCommand.getDocById(indexName, indexType, id), Duration.Inf)
    buildGetResponse(getResponse)
  }

  def findJSONElements(c: String): String => String= {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }
}

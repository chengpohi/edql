package com.github.chengpohi.parser

import com.github.chengpohi.base.ElasticCommand
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import fastparse.core.Parsed
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
object ELKCommand extends CollectionParser {
  val responseGenerator = new ResponseGenerator
  val TUPLE = """\(([^(),]+),([^(),]+)\)""".r


  import responseGenerator._

  val h: Seq[Any] => String = health
  val c: Seq[Any] => String = count
  val d: Seq[Any] => String = delete
  val q: Seq[Any] => String = query
  val r: Seq[Any] => String = reindex
  val i: Seq[Any] => String = index
  val u: Seq[Any] => String = update
  val ci: Seq[Any] => String = createIndex
  val a: Seq[Any] => String = analysis
  val gm: Seq[Any] => String = getMapping
  val gd: Seq[Any] => String = getDocById
  val m: Seq[Any] => String = mapping

  def getMapping(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualMappingsResponse: Future[GetMappingsResponse] = ElasticCommand.getMapping(indexName.asInstanceOf[String])
        val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
        buildGetMappingResponse(mappings)
      }
    }
  }

  def createIndex(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) =>
        val createResponse = ElasticCommand.createIndex(indexName.asInstanceOf[String])
        Await.result(createResponse, Duration.Inf).isAcknowledged.toString
    }
  }

  def health(parameters: Seq[Any]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName) =>
        ElasticCommand.countCommand(indexName.asInstanceOf[String])
    }
  }

  def delete(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case "*" => ElasticCommand.deleteIndex(indexName.asInstanceOf[String])
          case _ => ElasticCommand.deleteIndexType(indexName.asInstanceOf[String], indexType.asInstanceOf[String])
        }
        s"delete ${parameters.head} $indexType success"
      }
    }
  }

  def query(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case "*" => ElasticCommand.getAllDataByIndexName(indexName.asInstanceOf[String]).toString
          case indexType: String => ElasticCommand.getAllDataByIndexTypeWithIndexName(indexName.asInstanceOf[String], indexType).toString
        }
      }
    }
  }

  def update(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, updateFields) => {
        val uf: (String, String) = updateFields.asInstanceOf[String] match {
          case TUPLE(field, value) => (field.trim, value.trim)
          case _ => null
        }
        ElasticCommand.update(indexName.asInstanceOf[String], indexType.asInstanceOf[String], uf)
      }
    }
  }

  def reindex(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
        ElasticCommand.reindex(sourceIndex.asInstanceOf[String], targetIndex.asInstanceOf[String], sourceIndexType.asInstanceOf[String],
          fields.asInstanceOf[String].split(",").map(_.trim))
      }
    }
  }

  def index(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val uf: (String, String) = fields.asInstanceOf[String] match {
          case TUPLE(field, value) => (field.trim, value.trim)
          case _ => null
        }
        val indexResponse = ElasticCommand.indexField(indexName.asInstanceOf[String], indexType.asInstanceOf[String], uf)
        Await.result(indexResponse, Duration.Inf).getId
      }
      case Seq(indexName, indexType, fields, id) => {
        val uf: (String, String) = fields.asInstanceOf[String] match {
          case TUPLE(field, value) => (field.trim, value.trim)
          case _ => null
        }
        val indexResponse = ElasticCommand.indexFieldById(indexName.asInstanceOf[String], indexType.asInstanceOf[String], uf, id.asInstanceOf[String])
        Await.result(indexResponse, Duration.Inf).getId
      }
    }
  }

  def analysis(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(analyzer, doc) => {
        val analyzeResponse: AnalyzeResponse = Await.result(ElasticCommand.analysis(analyzer.asInstanceOf[String], doc.asInstanceOf[String]), Duration.Inf)
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
        val mappings: Future[CreateIndexResponse] = ElasticCommand.mappings(indexName.asInstanceOf[String], indexType.asInstanceOf[String], typeDefinitions.toIterable)
        val result: CreateIndexResponse = Await.result(mappings, Duration.Inf)
        buildCreateIndexResponse(result)
      }
    }
  }


  def getDocById(parameters: Seq[Any]): String = {
    parameters match {
      case Seq(indexName, indexType, id) => {
        val getResponse: GetResponse = Await.result(ElasticCommand.getDocById(indexName.asInstanceOf[String],
          indexType.asInstanceOf[String], id.asInstanceOf[String]), Duration.Inf)
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
}

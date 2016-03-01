package com.github.chengpohi.parser

import com.github.chengpohi.base.ElasticCommand
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import com.github.chengpohi.collection.JsonCollection._
import org.elasticsearch.action.search.SearchResponse

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
  val al: Seq[Val] => String = alias
  val cr: Seq[Val] => String = createRepository
  val cs: Seq[Val] => String = createSnapshot
  val ds: Seq[Val] => String = deleteSnapshot
  val gs: Seq[Val] => String = getSnapshot

  def getMapping(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualMappingsResponse: Future[GetMappingsResponse] = ElasticCommand.getMapping(indexName.extract[String])
        val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
        buildGetMappingResponse(mappings)
      }
    }
  }

  def createIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        val createResponse = ElasticCommand.createIndex(indexName.extract[String])
        Await.result(createResponse, Duration.Inf).isAcknowledged.toString
    }
  }

  def health(parameters: Seq[Val]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        ElasticCommand.countCommand(indexName.extract[String])
    }
  }

  def delete(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case Str("*") => ElasticCommand.deleteIndex(indexName.extract[String])
          case _ => ElasticCommand.deleteIndexType(indexName.extract[String], indexType.extract[String])
        }
        s"delete ${indexName.value} ${indexType.value} success"
      }
    }
  }

  def query(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType) => {
        indexType match {
          case Str("*") => ElasticCommand.getAllDataByIndexName(indexName.extract[String]).toString
          case _ => ElasticCommand.getAllDataByIndexTypeWithIndexName(indexName.extract[String],
            indexType.extract[String]).toString
        }
      }
    }
  }

  def update(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, updateFields) => {
        ElasticCommand.update(indexName.extract[String], indexType.extract[String], updateFields.extract[List[(String, String)]])
      }
    }
  }

  def reindex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
        ElasticCommand.reindex(sourceIndex.extract[String],
          targetIndex.extract[String], sourceIndexType.extract[String], fields.extract[List[String]])
      }
    }
  }

  def bulkIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val bulkResponse =
          ElasticCommand.bulkIndex(indexName.extract[String], indexType.extract[String], fields.extract[List[List[(String, String)]]])
        val br: BulkResponse = Await.result(bulkResponse, Duration.Inf)
        buildBulkResponse(br)
      }
    }
  }

  def index(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val indexResponse =
          ElasticCommand.indexField(indexName.extract[String], indexType.extract[String], fields.extract[List[(String, String)]])
        Await.result(indexResponse, Duration.Inf).getId
      }
      case Seq(indexName, indexType, fields, id) => {
        val indexResponse = ElasticCommand.indexFieldById(indexName.extract[String], indexType.extract[String],
          fields.extract[List[(String, String)]], id.extract[String])
        Await.result(indexResponse, Duration.Inf).getId
      }
    }
  }

  def analysis(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(analyzer, doc) => {
        val analyzeResponse: AnalyzeResponse = Await.result(ElasticCommand.analysis(analyzer.extract[String],
          doc.extract[String]), Duration.Inf)
        buildAnalyzeResponse(analyzeResponse)
      }
    }
  }

  def mapping(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, mapping) => {
        val mappings: Future[CreateIndexResponse] =
          ElasticCommand.mappings(indexName.extract[String], mapping.toJson)
        val result: CreateIndexResponse = Await.result(mappings, Duration.Inf)
        buildAcknowledgedResponse(result)
      }
    }
  }

  def aggsCount(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, rawJson) => {
        val aggsSearch: Future[SearchResponse] =
          ElasticCommand.aggsSearch(indexName.extract[String], indexType.extract[String], rawJson.toJson)
        val searchResponse: SearchResponse = Await.result(aggsSearch, Duration.Inf)
        buildSearchResponse(searchResponse)
      }
    }
  }

  def alias(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(targetIndex, sourceIndex) => {
        val eventualAliasesResponse: Future[IndicesAliasesResponse] =
          ElasticCommand.alias(targetIndex.extract[String], sourceIndex.extract[String])
        val aliasesResponse: IndicesAliasesResponse = Await.result(eventualAliasesResponse, Duration.Inf)
        buildAcknowledgedResponse(aliasesResponse)
      }
    }
  }


  def getDocById(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, id) => {
        val getResponse: GetResponse = Await.result(ElasticCommand.getDocById(indexName.extract[String],
          indexType.extract[String], id.extract[String]), Duration.Inf)
        buildGetResponse(getResponse)
      }
    }
  }

  def createRepository(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(repositoryName, repositoryType, settings) => {
        val repositoryResponse: PutRepositoryResponse = Await.result(
          ElasticCommand.createRepository(repositoryName.extract[String], repositoryType.extract[String],
            settings.extract[Map[String, String]]), Duration.Inf)
        buildAcknowledgedResponse(repositoryResponse)
      }
    }
  }

  def createSnapshot(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(snapshotName, repositoryName) => {
        val createSnapshotResponse: CreateSnapshotResponse = Await.result(
          ElasticCommand.createSnapshot(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
        buildXContent(createSnapshotResponse)
      }
    }
  }

  def deleteSnapshot(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(snapshotName, repositoryName) => {
        val deleteSnapshotResponse: DeleteSnapshotResponse = Await.result(
          ElasticCommand.deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
        buildAcknowledgedResponse(deleteSnapshotResponse)
      }
    }
  }

  def getSnapshot(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(snapshotName, repositoryName) => {
        val getSnapshotResponse: GetSnapshotsResponse = Await.result(
          ElasticCommand.getSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
        buildXContent(getSnapshotResponse)
      }
      case Seq(repositoryName) => {
        val getSnapshotResponse: GetSnapshotsResponse = Await.result(
          ElasticCommand.getAllSnapshotByRepositoryName(repositoryName.extract[String]), Duration.Inf)
        buildXContent(getSnapshotResponse)
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

package com.github.chengpohi.parser

import com.github.chengpohi.base.ElasticCommand
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.{RichGetResponse, RichSearchResponse, BulkResult}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, StringType}
import com.sksamuel.elastic4s.mappings.GetMappingsResult
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
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
  val rs: Seq[Val] => String = restoreSnapshot
  val clI: Seq[Val] => String = closeIndex
  val oi: Seq[Val] => String = openIndex

  def getMapping(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualMappingsResponse: Future[GetMappingsResult] = ElasticCommand.getMapping(indexName.extract[String])
        val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
        buildGetMappingResponse(mappings)
      }
    }
  }

  def createIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        val createResponse = ElasticCommand.createIndex(indexName.extract[String])
        buildAcknowledgedResponse(Await.result(createResponse, Duration.Inf))
    }
  }

  def health(parameters: Seq[Val]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) =>
        val eventualRichSearchResponse: Future[RichSearchResponse] = ElasticCommand.countCommand(indexName.extract[String])
        buildXContent(Await.result(eventualRichSearchResponse, Duration.Inf).original)
    }
  }

  def delete(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val eventualDeleteIndexResponse: Future[DeleteIndexResponse] = ElasticCommand.deleteIndex(indexName.extract[String])
        buildAcknowledgedResponse(Await.result(eventualDeleteIndexResponse, Duration.Inf))
      }
    }
  }

  def query(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType) =>
        buildXContent(ElasticCommand.getAll(indexName.extract[String], indexType.extract[String]).original)
      case Seq(indexName, indexType, queryData) =>
        val eventualRichSearchResponse: Future[RichSearchResponse] = ElasticCommand.queryDataByRawQuery(
          indexName.extract[String],
          indexType.extract[String],
          queryData.extract[Map[String, String]].toList)
        buildXContent(Await.result(eventualRichSearchResponse, Duration.Inf).original)
      case Seq(indexName) =>
        buildXContent(ElasticCommand.getAll(indexName.extract[String], "*").original)
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
        val br: BulkResult = Await.result(bulkResponse, Duration.Inf)
        buildBulkResponse(br)
      }
    }
  }

  def index(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName, indexType, fields) => {
        val indexResponse =
          ElasticCommand.indexField(indexName.extract[String], indexType.extract[String], fields.extract[List[(String, String)]])
        val created: Boolean = Await.result(indexResponse, Duration.Inf).original.isCreated
        buildIsCreated(created)
      }
      case Seq(indexName, indexType, fields, id) => {
        val indexResponse = ElasticCommand.indexFieldById(indexName.extract[String], indexType.extract[String],
          fields.extract[List[(String, String)]], id.extract[String])
        val created: Boolean = Await.result(indexResponse, Duration.Inf).original.isCreated
        buildIsCreated(created)
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
        val aggsSearch: Future[RichSearchResponse] =
          ElasticCommand.aggsSearch(indexName.extract[String], indexType.extract[String], rawJson.toJson)
        val searchResponse: RichSearchResponse = Await.result(aggsSearch, Duration.Inf)
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
        val getResponse: RichGetResponse = Await.result(ElasticCommand.getDocById(indexName.extract[String],
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

  def restoreSnapshot(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(snapshotName, repositoryName) => {
        val snapshotResponse: RestoreSnapshotResponse = Await.result(ElasticCommand.restoreSnapshot(snapshotName.extract[String],
          repositoryName.extract[String]), Duration.Inf)
        buildXContent(snapshotResponse)
      }
    }
  }

  def closeIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val closeIndexResponse: CloseIndexResponse = Await.result(ElasticCommand.closeIndex(indexName.extract[String]), Duration.Inf)
        buildAcknowledgedResponse(closeIndexResponse)
      }
    }
  }

  def openIndex(parameters: Seq[Val]): String = {
    parameters match {
      case Seq(indexName) => {
        val openIndexResponse: OpenIndexResponse = Await.result(ElasticCommand.openIndex(indexName.extract[String]), Duration.Inf)
        buildAcknowledgedResponse(openIndexResponse)
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

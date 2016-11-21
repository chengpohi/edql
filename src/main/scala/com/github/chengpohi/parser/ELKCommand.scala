package com.github.chengpohi.parser

import java.nio.file.{Files, Paths}

import com.github.chengpohi.api.ElasticDSL
import com.github.chengpohi.collection.JsonCollection._
import com.github.chengpohi.helper.ResponseGenerator
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.update.UpdateResponse

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticservice
  * Created by chengpohi on 1/18/16.
  */
class ELKCommand(val elasticCommand: ElasticDSL, val responseGenerator: ResponseGenerator) {

  import responseGenerator._
  import elasticCommand._

  def getMapping: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val eventualMappingsResponse: Future[GetMappingsResponse] = elasticCommand.getMapping(indexName)
      eventualMappingsResponse.map(buildGetMappingResponse)
    }
  }

  def createIndex: Seq[Val] => Future[String] = {
    {
      case Seq(indexName) =>
        val createResponse = elasticCommand.createIndex(indexName)
        createResponse.map(buildAcknowledgedResponse)
    }
  }

  def getIndices: Seq[Val] => Future[String] = _ => {
    val indiceResponse = elasticCommand.getIndices
    indiceResponse.map(s => buildXContent(s.getState))
  }

  def clusterStats: Seq[Val] => Future[String] = _ => {
    val clusterStatsResponse: Future[ClusterStatsResponse] = elasticCommand.clusterStats
    clusterStatsResponse.map(buildXContent)
  }

  def indicesStats: Seq[Val] => Future[String] = _ => {
    val indicesStatsResponse: Future[IndicesStatsResponse] = elasticCommand.indicesStats
    indicesStatsResponse.map(buildXContent)
  }

  def nodeStats: Seq[Val] => Future[String] = _ => {
    val nodesStatsResponse: Future[NodesStatsResponse] = elasticCommand.nodeStats
    nodesStatsResponse.map(s => buildXContent(s))
  }

  def clusterSettings: Seq[Val] => Future[String] = _ => {
    val clusterUpdateSettingsResponse: Future[ClusterUpdateSettingsResponse] = elasticCommand.clusterSettings
    clusterUpdateSettingsResponse.map(buildClusterSettingsResponse)
  }

  def nodeSettings: Seq[Val] => Future[String] = _ => {
    val nodesInfoResponse: Future[NodesInfoResponse] = elasticCommand.nodesSettings
    nodesInfoResponse.map(s => buildXContent(s))
  }

  def pendingTasks: Seq[Val] => Future[String] = _ => {
    val clusterTasksResponse: Future[PendingClusterTasksResponse] = elasticCommand.pendingTasks
    clusterTasksResponse.map(s => buildXContent(s))
  }

  def indexSettings: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val getSettingsResponse: Future[GetSettingsResponse] = elasticCommand.indexSettings(indexName)
      getSettingsResponse.map(s => buildGetSettingsResponse(s))
    }
  }

  def health: Seq[Val] => Future[String] = _ => {
    elasticCommand.clusterHealth.map(s => s.toString)
  }

  def shutdown: Seq[Val] => Future[String] = _ => {
    elasticCommand.shutdown
  }

  def count: Seq[Val] => Future[String] = {
    case Seq(indexName) =>
      val eventualRichSearchResponse: Future[SearchResponse] = elasticCommand.countCommand(indexName)
      eventualRichSearchResponse.map(s => buildXContent(s))
  }

  def delete: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val eventualDeleteIndexResponse: Future[DeleteIndexResponse] = elasticCommand.deleteIndex(indexName)
      eventualDeleteIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
    case Seq(indexName, indexType, id) => {
      val eventualDeleteIndexResponse: Future[DeleteResponse] = elasticCommand.deleteById(indexName,
        indexType, id)
      eventualDeleteIndexResponse.map(s => buildIsFound(s))
    }
  }

  def matchQuery: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, queryData) => {
      val eventualRichSearchResponse: Future[SearchResponse] = elasticCommand.matchQuery(
        indexName,
        indexType,
        queryData.extract[Map[String, String]].toList.head)
      eventualRichSearchResponse.map(s => buildXContent(s))
    }
  }

  def query: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType) =>
      elasticCommand.queryAll(indexName, indexType).map(s => buildXContent(s))
    case Seq(indexName, indexType, queryData) =>
      val eventualRichSearchResponse: Future[SearchResponse] = elasticCommand.termQuery(
        indexName,
        indexType,
        queryData.extract[Map[String, String]].toList)
      eventualRichSearchResponse.map(s => buildXContent(s))
    case Seq(indexName) =>
      elasticCommand.queryAll(indexName, "*").map(s => buildXContent(s))
    case Seq(indexName, indexType, joinIndexName, joinIndexType, field) =>
      Future.sequence(elasticCommand.joinQuery(indexName, indexType,
        joinIndexName, joinIndexType, field)).map(buildStreamMapTupels)
  }

  def update: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, updateFields) => {
      elasticCommand.updateAllDocs(indexName, indexType, updateFields.extract[List[(String, String)]])
    }
    case Seq(indexName, indexType, updateFields, id) => {
      val eventualUpdateResponse: Future[UpdateResponse] = elasticCommand.updateById(indexName,
        indexType,
        updateFields.extract[List[(String, String)]],
        id)
      eventualUpdateResponse.map(s => buildXContent(s.getShardInfo))
    }
  }

  def reindex: Seq[Val] => Future[String] = {
    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
      elasticCommand.reindex(sourceIndex,
        targetIndex, sourceIndexType, fields.extract[List[String]])
    }
  }

  def bulkIndex: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, fields) => {
      elasticCommand.bulkIndex(indexName, indexType, fields.extract[List[List[(String, String)]]])
      Future {
        buildIsCreated(true)
      }
    }
  }

  def index: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, fields) => {
      val indexResponse =
        elasticCommand.indexField(indexName, indexType, fields.extract[List[(String, String)]])
      indexResponse.map(s => buildIdResponse(s.getId))
    }
    case Seq(indexName, indexType, fields, id) => {
      val indexResponse = elasticCommand.indexFieldById(indexName, indexType,
        fields.extract[List[(String, String)]], id)
      indexResponse.map(s => buildIdResponse(s.getId))
    }
  }

  def analysis: Seq[Val] => Future[String] = {
    case Seq(doc, analyzer) => {
      val analyzeResponse: Future[AnalyzeResponse] = elasticCommand.analysiser(analyzer, doc)
      analyzeResponse.map(s => buildAnalyzeResponse(s))
    }
  }

  def createAnalyzer: Seq[Val] => Future[String] = {
    case Seq(analyzer) => {
      val analysisSettings = Obj(("analysis", analyzer))
      val analyzeResponse: Future[UpdateSettingsResponse] = elasticCommand.createAnalyzer(analysisSettings.toJson)
      analyzeResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def mapping: Seq[Val] => Future[String] = {
    case Seq(indexName, mapping) => {
      val mappings: Future[CreateIndexResponse] =
        elasticCommand.mappings(indexName, mapping.toJson)
      mappings.map(s => buildAcknowledgedResponse(s))
    }
  }

  def updateMapping: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, mapping) => {
      val mappings: Future[PutMappingResponse] =
        elasticCommand.updateMappings(indexName, indexType, mapping.toJson)
      mappings.map(s => buildAcknowledgedResponse(s))
    }
  }

  def aggsCount: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, name) => {
      val aggsSearch: Future[SearchResponse] =
        elasticCommand.aggsSearch(indexName, indexType, name)
      aggsSearch.map(s => buildSearchResponse(s))
    }
  }

  def aggsTerm: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, name) => {
      val aggsSearch: Future[SearchResponse] =
        elasticCommand.termsSearch(indexName, indexType, name)
      aggsSearch.map(s => buildSearchResponse(s))
    }
  }

  def histAggs: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, name, _interval, _field) => {
      val aggsSearch: Future[SearchResponse] = DSL {
        aggs in indexName / indexType hist name interval _interval field _field
      }
      aggsSearch.map(s => buildSearchResponse(s))
    }
  }

  def alias: Seq[Val] => Future[String] = {
    case Seq(targetIndex, sourceIndex) => {
      val eventualAliasesResponse: Future[IndicesAliasesResponse] =
        elasticCommand.alias(targetIndex, sourceIndex)
      eventualAliasesResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def getDocById: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, id) => {
      val getResponse: Future[GetResponse] = elasticCommand.getDocById(indexName,
        indexType, id)
      getResponse.map(s => buildGetResponse(s))
    }
  }

  def createRepository: Seq[Val] => Future[String] = {
    case Seq(repositoryName, repositoryType, settings) => {
      val repositoryResponse: Future[PutRepositoryResponse] =
        elasticCommand.createRepository(repositoryName, repositoryType,
          settings.extract[Map[String, String]])
      repositoryResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def createSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val createSnapshotResponse: Future[CreateSnapshotResponse] =
        elasticCommand.createSnapshot(snapshotName, repositoryName)
      createSnapshotResponse.map(s => buildXContent(s))
    }
  }

  def deleteSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val deleteSnapshotResponse: Future[DeleteSnapshotResponse] =
        elasticCommand.deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName, repositoryName)
      deleteSnapshotResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def restoreSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val snapshotResponse: Future[RestoreSnapshotResponse] = elasticCommand.restoreSnapshot(snapshotName,
        repositoryName)
      snapshotResponse.map(s => buildXContent(s))
    }
  }

  def closeIndex: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val closeIndexResponse: Future[CloseIndexResponse] = elasticCommand.closeIndex(indexName)
      closeIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def openIndex: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val openIndexResponse: Future[OpenIndexResponse] = elasticCommand.openIndex(indexName)
      openIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def dumpIndex: Seq[Val] => Future[String] = {
    case Seq(indexName, fileName) => {
      val _fileName = fileName
      val path = Paths.get(_fileName)
      val searchResponse = DSL {
        search in indexName size Integer.MAX_VALUE
      }
      searchResponse.map(f => {
        val res = f.getHits.asScala.map(i => {
          s"""index into "${i.index()}" / "${i.`type`()}" fields ${i.getSourceAsString}"""
        })
        Files.write(path, res.asJava)
        path.toUri.toString
      })
    }
  }

  def getSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val getSnapshotResponse: Future[GetSnapshotsResponse] =
        elasticCommand.getSnapshotBySnapshotNameAndRepositoryName(snapshotName, repositoryName)
      getSnapshotResponse.map(s => buildXContent(s))
    }
    case Seq(repositoryName) => {
      val getSnapshotResponse: Future[GetSnapshotsResponse] =
        elasticCommand.getAllSnapshotByRepositoryName(repositoryName)
      getSnapshotResponse.map(s => buildXContent(s))
    }
  }

  def waitForStatus: Seq[Val] => Future[String] = {
    case Seq(status) => {
      val healthResponse: Future[ClusterHealthResponse] =
        elasticCommand.waitForStatus(status = Some(status))
      healthResponse.map(s => buildXContent(s))
    }
  }

  def findJSONElements(c: String): String => String = {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }
  implicit def valToString(v: Val): String = v.extract[String]
}

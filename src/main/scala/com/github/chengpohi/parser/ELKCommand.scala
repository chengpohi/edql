package com.github.chengpohi.parser

import com.github.chengpohi.api.ElasticCommand
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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticservice
  * Created by chengpohi on 1/18/16.
  */
class ELKCommand(val elasticCommand: ElasticCommand, val responseGenerator: ResponseGenerator) {

  import responseGenerator._

  def getMapping: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val eventualMappingsResponse: Future[GetMappingsResponse] = elasticCommand.getMapping(indexName.extract[String])
      eventualMappingsResponse.map(buildGetMappingResponse)
    }
  }

  def createIndex: Seq[Val] => Future[String] = {
    {
      case Seq(indexName) =>
        val createResponse = elasticCommand.createIndex(indexName.extract[String])
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
      val getSettingsResponse: Future[GetSettingsResponse] = elasticCommand.indexSettings(indexName.extract[String])
      getSettingsResponse.map(s => buildGetSettingsResponse(s))
    }
  }

  def health: Seq[Val] => Future[String] = _ => {
    elasticCommand.clusterHealth.map(s => s.toString)
  }

  def count: Seq[Val] => Future[String] = {
    case Seq(indexName) =>
      val eventualRichSearchResponse: Future[SearchResponse] = elasticCommand.countCommand(indexName.extract[String])
      eventualRichSearchResponse.map(s => buildXContent(s))
  }

  def delete: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val eventualDeleteIndexResponse: Future[DeleteIndexResponse] = elasticCommand.deleteIndex(indexName.extract[String])
      eventualDeleteIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
    case Seq(indexName, indexType, id) => {
      val eventualDeleteIndexResponse: Future[DeleteResponse] = elasticCommand.deleteById(indexName.extract[String],
        indexType.extract[String], id.extract[String])
      eventualDeleteIndexResponse.map(s => buildIsFound(s))
    }
  }

  def query: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType) =>
      elasticCommand.queryAll(indexName.extract[String], indexType.extract[String]).map(s => buildXContent(s))
    case Seq(indexName, indexType, queryData) =>
      val eventualRichSearchResponse: Future[SearchResponse] = elasticCommand.queryDataByRawQuery(
        indexName.extract[String],
        indexType.extract[String],
        queryData.extract[Map[String, String]].toList)
      eventualRichSearchResponse.map(s => buildXContent(s))
    case Seq(indexName) =>
      elasticCommand.queryAll(indexName.extract[String], "*").map(s => buildXContent(s))
    case Seq(indexName, indexType, joinIndexName, joinIndexType, field) =>
      Future.sequence(elasticCommand.joinQuery(indexName.extract[String], indexType.extract[String],
        joinIndexName.extract[String], joinIndexType.extract[String], field.extract[String])).map(buildStreamMapTupels)
  }

  def update: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, updateFields) => {
      elasticCommand.updateAllDocs(indexName.extract[String], indexType.extract[String], updateFields.extract[List[(String, String)]])
    }
    case Seq(indexName, indexType, updateFields, id) => {
      val eventualUpdateResponse: Future[UpdateResponse] = elasticCommand.updateById(indexName.extract[String],
        indexType.extract[String],
        updateFields.extract[List[(String, String)]],
        id.extract[String])
      eventualUpdateResponse.map(s => buildXContent(s.getShardInfo))
    }
  }

  def reindex: Seq[Val] => Future[String] = {
    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
      elasticCommand.reindex(sourceIndex.extract[String],
        targetIndex.extract[String], sourceIndexType.extract[String], fields.extract[List[String]])
    }
  }

  def bulkIndex: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, fields) => {
      elasticCommand.bulkIndex(indexName.extract[String], indexType.extract[String], fields.extract[List[List[(String, String)]]])
      Future {
        buildIsCreated(true)
      }
    }
  }

  def index: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, fields) => {
      val indexResponse =
        elasticCommand.indexField(indexName.extract[String], indexType.extract[String], fields.extract[List[(String, String)]])
      indexResponse.map(s => buildIdResponse(s.getId))
    }
    case Seq(indexName, indexType, fields, id) => {
      val indexResponse = elasticCommand.indexFieldById(indexName.extract[String], indexType.extract[String],
        fields.extract[List[(String, String)]], id.extract[String])
      indexResponse.map(s => buildIdResponse(s.getId))
    }
  }

  def analysis: Seq[Val] => Future[String] = {
    case Seq(analyzer, doc) => {
      val analyzeResponse: Future[AnalyzeResponse] = elasticCommand.analysiser(analyzer.extract[String], doc.extract[String])
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
        elasticCommand.mappings(indexName.extract[String], mapping.toJson)
      mappings.map(s => buildAcknowledgedResponse(s))
    }
  }

  def updateMapping: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, mapping) => {
      val mappings: Future[PutMappingResponse] =
        elasticCommand.updateMappings(indexName.extract[String], indexType.extract[String], mapping.toJson)
      mappings.map(s => buildAcknowledgedResponse(s))
    }
  }

  def aggsCount: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, rawJson) => {
      val aggsSearch: Future[SearchResponse] =
        elasticCommand.aggsSearch(indexName.extract[String], indexType.extract[String], rawJson.toJson)
      aggsSearch.map(s => buildSearchResponse(s))
    }
  }

  def alias: Seq[Val] => Future[String] = {
    case Seq(targetIndex, sourceIndex) => {
      val eventualAliasesResponse: Future[IndicesAliasesResponse] =
        elasticCommand.alias(targetIndex.extract[String], sourceIndex.extract[String])
      eventualAliasesResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def getDocById: Seq[Val] => Future[String] = {
    case Seq(indexName, indexType, id) => {
      val getResponse: Future[GetResponse] = elasticCommand.getDocById(indexName.extract[String],
        indexType.extract[String], id.extract[String])
      getResponse.map(s => buildGetResponse(s))
    }
  }

  def createRepository: Seq[Val] => Future[String] = {
    case Seq(repositoryName, repositoryType, settings) => {
      val repositoryResponse: Future[PutRepositoryResponse] =
        elasticCommand.createRepository(repositoryName.extract[String], repositoryType.extract[String],
          settings.extract[Map[String, String]])
      repositoryResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def createSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val createSnapshotResponse: Future[CreateSnapshotResponse] =
        elasticCommand.createSnapshot(snapshotName.extract[String], repositoryName.extract[String])
      createSnapshotResponse.map(s => buildXContent(s))
    }
  }

  def deleteSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val deleteSnapshotResponse: Future[DeleteSnapshotResponse] =
        elasticCommand.deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String])
      deleteSnapshotResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def restoreSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val snapshotResponse: Future[RestoreSnapshotResponse] = elasticCommand.restoreSnapshot(snapshotName.extract[String],
        repositoryName.extract[String])
      snapshotResponse.map(s => buildXContent(s))
    }
  }

  def closeIndex: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val closeIndexResponse: Future[CloseIndexResponse] = elasticCommand.closeIndex(indexName.extract[String])
      closeIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def openIndex: Seq[Val] => Future[String] = {
    case Seq(indexName) => {
      val openIndexResponse: Future[OpenIndexResponse] = elasticCommand.openIndex(indexName.extract[String])
      openIndexResponse.map(s => buildAcknowledgedResponse(s))
    }
  }

  def getSnapshot: Seq[Val] => Future[String] = {
    case Seq(snapshotName, repositoryName) => {
      val getSnapshotResponse: Future[GetSnapshotsResponse] =
        elasticCommand.getSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String])
      getSnapshotResponse.map(s => buildXContent(s))
    }
    case Seq(repositoryName) => {
      val getSnapshotResponse: Future[GetSnapshotsResponse] =
        elasticCommand.getAllSnapshotByRepositoryName(repositoryName.extract[String])
      getSnapshotResponse.map(s => buildXContent(s))
    }
  }

  def waitForStatus: Seq[Val] => Future[String] = {
    case Seq(status) => {
      val healthResponse: Future[ClusterHealthResponse] =
        elasticCommand.waitForStatus(status = Some(status.extract[String]))
      healthResponse.map(s => buildXContent(s))
    }
  }

  def findJSONElements(c: String): String => String = {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }
}

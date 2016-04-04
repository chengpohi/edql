package com.github.chengpohi.parser

import com.github.chengpohi.api.ElasticCommand
import com.github.chengpohi.collection.JsonCollection._
import com.github.chengpohi.helper.ResponseGenerator
import com.sksamuel.elastic4s.mappings.GetMappingsResult
import com.sksamuel.elastic4s.{BulkResult, RichGetResponse, RichSearchResponse}
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
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.update.UpdateResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * elasticservice
  * Created by chengpohi on 1/18/16.
  */
class ELKCommand(val elasticCommand: ElasticCommand, val responseGenerator: ResponseGenerator) {
  import responseGenerator._

  def getMapping: Seq[Val] => String = {
    case Seq(indexName) => {
      val eventualMappingsResponse: Future[GetMappingsResult] = elasticCommand.getMapping(indexName.extract[String])
      val mappings = Await.result(eventualMappingsResponse, Duration.Inf)
      buildGetMappingResponse(mappings)
    }
  }

  def createIndex: Seq[Val] => String = {
    {
      case Seq(indexName) =>
        val createResponse = elasticCommand.createIndex(indexName.extract[String])
        buildAcknowledgedResponse(Await.result(createResponse, Duration.Inf))
    }
  }

  def getIndices: Seq[Val] => String = _ => {
    val eventualClusterStateResponse = elasticCommand.getIndices
    buildXContent(Await.result(eventualClusterStateResponse, Duration.Inf).getState)
  }

  def clusterStats: Seq[Val] => String = _ => {
    val clusterStatsResponse: ClusterStatsResponse = Await.result(elasticCommand.clusterStats, Duration.Inf)
    buildXContent(clusterStatsResponse)
  }

  def indicesStats: Seq[Val] => String = _ => {
    val indicesStatsResponse: IndicesStatsResponse = Await.result(elasticCommand.indicesStats, Duration.Inf)
    buildXContent(indicesStatsResponse)
  }

  def nodeStats: Seq[Val] => String = _ => {
    val nodesStatsResponse: NodesStatsResponse = Await.result(elasticCommand.nodeStats, Duration.Inf)
    buildXContent(nodesStatsResponse)
  }

  def clusterSettings: Seq[Val] => String = _ => {
    val clusterUpdateSettingsResponse: ClusterUpdateSettingsResponse = Await.result(elasticCommand.clusterSettings(), Duration.Inf)
    buildClusterSettingsResponse(clusterUpdateSettingsResponse)
  }

  def nodeSettings: Seq[Val] => String = _ => {
    val nodesInfoResponse: NodesInfoResponse = Await.result(elasticCommand.nodesSettings(), Duration.Inf)
    buildXContent(nodesInfoResponse)
  }

  def pendingTasks: Seq[Val] => String = _ => {
    val clusterTasksResponse: PendingClusterTasksResponse = Await.result(elasticCommand.pendingTasks(), Duration.Inf)
    buildXContent(clusterTasksResponse)
  }

  def indexSettings: Seq[Val] => String = {
    case Seq(indexName) => {
      val getSettingsResponse: GetSettingsResponse = Await.result(elasticCommand.indexSettings(indexName.extract[String]), Duration.Inf)
      buildGetSettingsResponse(getSettingsResponse)
    }
  }

  def health: Seq[Val] => String = _ => {
    elasticCommand.clusterHealth()
  }

  def count: Seq[Val] => String = {
    case Seq(indexName) =>
      val eventualRichSearchResponse: Future[RichSearchResponse] = elasticCommand.countCommand(indexName.extract[String])
      buildXContent(Await.result(eventualRichSearchResponse, Duration.Inf).original)
  }

  def delete: Seq[Val] => String = {
    case Seq(indexName) => {
      val eventualDeleteIndexResponse: Future[DeleteIndexResponse] = elasticCommand.deleteIndex(indexName.extract[String])
      buildAcknowledgedResponse(Await.result(eventualDeleteIndexResponse, Duration.Inf))
    }
    case Seq(indexName, indexType, id) => {
      val eventualDeleteIndexResponse: Future[DeleteResponse] = elasticCommand.deleteById(indexName.extract[String],
        indexType.extract[String], id.extract[String])
      buildIsFound(Await.result(eventualDeleteIndexResponse, Duration.Inf))
    }
  }

  def query: Seq[Val] => String = {
    case Seq(indexName, indexType) =>
      buildXContent(elasticCommand.getAll(indexName.extract[String], indexType.extract[String]).original)
    case Seq(indexName, indexType, queryData) =>
      val eventualRichSearchResponse: Future[RichSearchResponse] = elasticCommand.queryDataByRawQuery(
        indexName.extract[String],
        indexType.extract[String],
        queryData.extract[Map[String, String]].toList)
      buildXContent(Await.result(eventualRichSearchResponse, Duration.Inf).original)
    case Seq(indexName) =>
      buildXContent(elasticCommand.getAll(indexName.extract[String], "*").original)
  }

  def update: Seq[Val] => String = {
    case Seq(indexName, indexType, updateFields) => {
      elasticCommand.updateAllDocs(indexName.extract[String], indexType.extract[String], updateFields.extract[List[(String, String)]])
    }
    case Seq(indexName, indexType, updateFields, id) => {
      val eventualUpdateResponse: Future[UpdateResponse] = elasticCommand.updateById(indexName.extract[String],
        indexType.extract[String],
        updateFields.extract[List[(String, String)]],
        id.extract[String])
      buildXContent(Await.result(eventualUpdateResponse, Duration.Inf).getShardInfo)
    }
  }

  def reindex: Seq[Val] => String = {
    case Seq(sourceIndex, targetIndex, sourceIndexType, fields) => {
      elasticCommand.reindex(sourceIndex.extract[String],
        targetIndex.extract[String], sourceIndexType.extract[String], fields.extract[List[String]])
    }
  }

  def bulkIndex: Seq[Val] => String = {
    case Seq(indexName, indexType, fields) => {
      val bulkResponse =
        elasticCommand.bulkIndex(indexName.extract[String], indexType.extract[String], fields.extract[List[List[(String, String)]]])
      val br: BulkResult = Await.result(bulkResponse, Duration.Inf)
      buildBulkResponse(br)
    }
  }

  def index: Seq[Val] => String = {
    case Seq(indexName, indexType, fields) => {
      val indexResponse =
        elasticCommand.indexField(indexName.extract[String], indexType.extract[String], fields.extract[List[(String, String)]])
      val created: Boolean = Await.result(indexResponse, Duration.Inf).original.isCreated
      buildIsCreated(created)
    }
    case Seq(indexName, indexType, fields, id) => {
      val indexResponse = elasticCommand.indexFieldById(indexName.extract[String], indexType.extract[String],
        fields.extract[List[(String, String)]], id.extract[String])
      val created: Boolean = Await.result(indexResponse, Duration.Inf).original.isCreated
      buildIsCreated(created)
    }
  }

  def analysis: Seq[Val] => String = {
    case Seq(analyzer, doc) => {
      val analyzeResponse: AnalyzeResponse = Await.result(elasticCommand.analysis(analyzer.extract[String],
        doc.extract[String]), Duration.Inf)
      buildAnalyzeResponse(analyzeResponse)
    }
  }

  def mapping: Seq[Val] => String = {
    case Seq(indexName, mapping) => {
      val mappings: Future[CreateIndexResponse] =
        elasticCommand.mappings(indexName.extract[String], mapping.toJson)
      val result: CreateIndexResponse = Await.result(mappings, Duration.Inf)
      buildAcknowledgedResponse(result)
    }
  }

  def aggsCount: Seq[Val] => String = {
    case Seq(indexName, indexType, rawJson) => {
      val aggsSearch: Future[RichSearchResponse] =
        elasticCommand.aggsSearch(indexName.extract[String], indexType.extract[String], rawJson.toJson)
      val searchResponse: RichSearchResponse = Await.result(aggsSearch, Duration.Inf)
      buildSearchResponse(searchResponse)
    }
  }

  def alias: Seq[Val] => String = {
    case Seq(targetIndex, sourceIndex) => {
      val eventualAliasesResponse: Future[IndicesAliasesResponse] =
        elasticCommand.alias(targetIndex.extract[String], sourceIndex.extract[String])
      val aliasesResponse: IndicesAliasesResponse = Await.result(eventualAliasesResponse, Duration.Inf)
      buildAcknowledgedResponse(aliasesResponse)
    }
  }


  def getDocById: Seq[Val] => String = {
    case Seq(indexName, indexType, id) => {
      val getResponse: RichGetResponse = Await.result(elasticCommand.getDocById(indexName.extract[String],
        indexType.extract[String], id.extract[String]), Duration.Inf)
      buildGetResponse(getResponse)
    }
  }

  def createRepository: Seq[Val] => String = {
    case Seq(repositoryName, repositoryType, settings) => {
      val repositoryResponse: PutRepositoryResponse = Await.result(
        elasticCommand.createRepository(repositoryName.extract[String], repositoryType.extract[String],
          settings.extract[Map[String, String]]), Duration.Inf)
      buildAcknowledgedResponse(repositoryResponse)
    }
  }

  def createSnapshot: Seq[Val] => String = {
    case Seq(snapshotName, repositoryName) => {
      val createSnapshotResponse: CreateSnapshotResponse = Await.result(
        elasticCommand.createSnapshot(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
      buildXContent(createSnapshotResponse)
    }
  }

  def deleteSnapshot: Seq[Val] => String = {
    case Seq(snapshotName, repositoryName) => {
      val deleteSnapshotResponse: DeleteSnapshotResponse = Await.result(
        elasticCommand.deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
      buildAcknowledgedResponse(deleteSnapshotResponse)
    }
  }

  def restoreSnapshot: Seq[Val] => String = {
    case Seq(snapshotName, repositoryName) => {
      val snapshotResponse: RestoreSnapshotResponse = Await.result(elasticCommand.restoreSnapshot(snapshotName.extract[String],
        repositoryName.extract[String]), Duration.Inf)
      buildXContent(snapshotResponse)
    }
  }

  def closeIndex: Seq[Val] => String = {
    case Seq(indexName) => {
      val closeIndexResponse: CloseIndexResponse = Await.result(elasticCommand.closeIndex(indexName.extract[String]), Duration.Inf)
      buildAcknowledgedResponse(closeIndexResponse)
    }
  }

  def openIndex: Seq[Val] => String = {
    case Seq(indexName) => {
      val openIndexResponse: OpenIndexResponse = Await.result(elasticCommand.openIndex(indexName.extract[String]), Duration.Inf)
      buildAcknowledgedResponse(openIndexResponse)
    }
  }

  def getSnapshot: Seq[Val] => String = {
    case Seq(snapshotName, repositoryName) => {
      val getSnapshotResponse: GetSnapshotsResponse = Await.result(
        elasticCommand.getSnapshotBySnapshotNameAndRepositoryName(snapshotName.extract[String], repositoryName.extract[String]), Duration.Inf)
      buildXContent(getSnapshotResponse)
    }
    case Seq(repositoryName) => {
      val getSnapshotResponse: GetSnapshotsResponse = Await.result(
        elasticCommand.getAllSnapshotByRepositoryName(repositoryName.extract[String]), Duration.Inf)
      buildXContent(getSnapshotResponse)
    }
  }

  def waitForStatus:Seq[Val] => String = {
    case Seq(status) => {
      val healthResponse: ClusterHealthResponse =
        Await.result(elasticCommand.waitForStatus(status = Some(status.extract[String])), Duration.Inf)
      buildXContent(healthResponse)
    }
  }

  def findJSONElements(c: String): String => String = {
    extractJSON(_, c)
  }

  def beautyJson(): String => String = {
    beautyJSON
  }
}

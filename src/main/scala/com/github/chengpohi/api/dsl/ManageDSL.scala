package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequestBuilder, ClusterHealthResponse}
import org.elasticsearch.action.admin.cluster.node.info.{NodesInfoRequestBuilder, NodesInfoResponse}
import org.elasticsearch.action.admin.cluster.node.stats.{NodesStatsRequestBuilder, NodesStatsResponse}
import org.elasticsearch.action.admin.cluster.repositories.put.{PutRepositoryRequestBuilder, PutRepositoryResponse}
import org.elasticsearch.action.admin.cluster.settings.{ClusterUpdateSettingsRequestBuilder, ClusterUpdateSettingsResponse}
import org.elasticsearch.action.admin.cluster.snapshots.create.{CreateSnapshotRequestBuilder, CreateSnapshotResponse}
import org.elasticsearch.action.admin.cluster.snapshots.delete.{DeleteSnapshotRequestBuilder, DeleteSnapshotResponse}
import org.elasticsearch.action.admin.cluster.snapshots.get.{GetSnapshotsRequestBuilder, GetSnapshotsResponse}
import org.elasticsearch.action.admin.cluster.snapshots.restore.{RestoreSnapshotRequestBuilder, RestoreSnapshotResponse}
import org.elasticsearch.action.admin.cluster.state.{ClusterStateRequestBuilder, ClusterStateResponse}
import org.elasticsearch.action.admin.cluster.stats.{ClusterStatsRequestBuilder, ClusterStatsResponse}
import org.elasticsearch.action.admin.cluster.tasks.{PendingClusterTasksRequestBuilder, PendingClusterTasksResponse}
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequestBuilder, IndicesAliasesResponse}
import org.elasticsearch.action.admin.indices.close.{CloseIndexRequestBuilder, CloseIndexResponse}
import org.elasticsearch.action.admin.indices.create.{CreateIndexRequestBuilder, CreateIndexResponse}
import org.elasticsearch.action.admin.indices.mapping.get.{GetMappingsRequestBuilder, GetMappingsResponse}
import org.elasticsearch.action.admin.indices.open.{OpenIndexRequestBuilder, OpenIndexResponse}
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequestBuilder, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.stats.{IndicesStatsRequestBuilder, IndicesStatsResponse}
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse}
import org.elasticsearch.cluster.health.ClusterHealthStatus

import scala.collection.JavaConverters._

/**
  * elasticshell
  * Created by chengpohi on 6/26/16.
  */
trait ManageDSL extends ElasticBase {
  abstract class FlagType
  case object FlagType {
    case object ALL extends FlagType
  }

  abstract class NodeType {
    def value: Array[String]
  }
  case object NodeType {
    case object ALL extends NodeType {
      override def value: Array[String] = Array()
    }
  }

  case class NodeStatsRequestDefinition(nodesStatsRequestBuilder: NodesStatsRequestBuilder) extends ActionRequest[NodesStatsResponse] {
    def flag(f: FlagType) = {
      nodesStatsRequestBuilder.all()
      this
    }
    override def execute: (ActionListener[NodesStatsResponse]) => Unit = nodesStatsRequestBuilder.execute
  }

  case class NodeInfoRequestDefinition(nodesInfoRequestBuilder: NodesInfoRequestBuilder) extends ActionRequest[NodesInfoResponse] {
    override def execute: (ActionListener[NodesInfoResponse]) => Unit = nodesInfoRequestBuilder.execute
  }

  case object node {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: NodesStatsRequestBuilder = clusterClient.prepareNodesStats(nodeIds: _*)
      NodeStatsRequestDefinition(prepareNodesStats)
    }
    def stats(nodeIds: NodeType) = {
      val prepareNodesStats: NodesStatsRequestBuilder = clusterClient.prepareNodesStats(nodeIds.value: _*)
      NodeStatsRequestDefinition(prepareNodesStats)
    }
    def info = {
      val prepareNodesStats: NodesInfoRequestBuilder = clusterClient.prepareNodesInfo()
      NodeInfoRequestDefinition(prepareNodesStats)
    }
  }

  case class IndicesStatsRequestDefinition(indicesStatsRequestBuilder: IndicesStatsRequestBuilder) extends ActionRequest[IndicesStatsResponse] {
    def flag(f: FlagType) = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: (ActionListener[IndicesStatsResponse]) => Unit = indicesStatsRequestBuilder.execute
  }

  case object indice {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(nodeIds: _ *)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }
    def stats(indiceType: NodeType) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(indiceType.value: _*)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }
  }

  case class ClusterStatsRequestDefinition(clusterStatsRequestBuilder: ClusterStatsRequestBuilder) extends ActionRequest[ClusterStatsResponse] {
    override def execute: (ActionListener[ClusterStatsResponse]) => Unit = clusterStatsRequestBuilder.execute
  }

  case class ClusterHealthRequestDefinition(clusterHealthRequestBuilder: ClusterHealthRequestBuilder) extends ActionRequest[ClusterHealthResponse] {
    def timeout(time: String) = {
      clusterHealthRequestBuilder.setTimeout(time)
      this
    }

    def status(clusterHealth: ClusterHealthStatus) = {
      clusterHealthRequestBuilder.setWaitForStatus(clusterHealth)
      this
    }

    override def execute: (ActionListener[ClusterHealthResponse]) => Unit = clusterHealthRequestBuilder.execute
  }

  case class ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder) extends ActionRequest[ClusterUpdateSettingsResponse] {
    override def execute: (ActionListener[ClusterUpdateSettingsResponse]) => Unit = clusterUpdateSettingsRequestBuilder.execute
  }

  case object cluster {
    def stats = {
      val prepareNodesStats: ClusterStatsRequestBuilder = clusterClient.prepareClusterStats()
      ClusterStatsRequestDefinition(prepareNodesStats)
    }

    def state = {
      val stateRequestBuilder: ClusterStateRequestBuilder = clusterClient.prepareState()
      ClusterStateRequestDefinition(stateRequestBuilder)
    }
    def health = {
      val clusterHealthRequestBuilder: ClusterHealthRequestBuilder = clusterClient.prepareHealth()
      ClusterHealthRequestDefinition(clusterHealthRequestBuilder)
    }

    def settings = {
      val clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder = clusterClient.prepareUpdateSettings()
      ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder)
    }
  }

  case class PutRepositoryDefinition(putRepositoryRequestBuilder: PutRepositoryRequestBuilder) extends ActionRequest[PutRepositoryResponse] {
    def `type`(`type`: String) = {
      putRepositoryRequestBuilder.setType(`type`)
      this
    }

    def settings(st: Map[String, AnyRef]) = {
      putRepositoryRequestBuilder.setSettings(st.asJava)
      this
    }

    override def execute: (ActionListener[PutRepositoryResponse]) => Unit = putRepositoryRequestBuilder.execute
  }

  case class CreateSnapshotDefinition(snapshotName: String) extends ActionRequest[CreateSnapshotResponse] {
    var createSnapshotRequestBuilder: CreateSnapshotRequestBuilder = null
    def in(repositoryName: String) = {
      createSnapshotRequestBuilder = clusterClient.prepareCreateSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[CreateSnapshotResponse]) => Unit = createSnapshotRequestBuilder.execute
  }

  case class CreateIndexDefinition(createIndexRequestBuilder: CreateIndexRequestBuilder) extends ActionRequest[CreateIndexResponse] {
    def mappings(m: String) = {
      createIndexRequestBuilder.setSource(m)
      this
    }
    override def execute: (ActionListener[CreateIndexResponse]) => Unit = createIndexRequestBuilder.execute
  }

  case class GetSnapshotDefinition(snapshotName: String) extends ActionRequest[GetSnapshotsResponse] {
    var getSnapshotsRequestBuilder: GetSnapshotsRequestBuilder = null
    def from(repositoryName: String) = {
      getSnapshotsRequestBuilder = snapshotName match {
        case "*" => clusterClient.prepareGetSnapshots(repositoryName).setSnapshots(snapshotName)
        case _ => clusterClient.prepareGetSnapshots(repositoryName)
      }
      this
    }

    override def execute: (ActionListener[GetSnapshotsResponse]) => Unit = getSnapshotsRequestBuilder.execute
  }

  case class DeleteSnapshotDefinition(snapshotName: String) extends ActionRequest[DeleteSnapshotResponse] {
    var deleteSnapshotRequestBuilder: DeleteSnapshotRequestBuilder = null
    def from(repositoryName: String) = {
      deleteSnapshotRequestBuilder = clusterClient.prepareDeleteSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[DeleteSnapshotResponse]) => Unit = deleteSnapshotRequestBuilder.execute
  }

  case class GetMappingDefinition(getMappingsRequestBuilder: GetMappingsRequestBuilder) extends ActionRequest[GetMappingsResponse] {
    override def execute: (ActionListener[GetMappingsResponse]) => Unit = getMappingsRequestBuilder.execute
  }

  case class ClusterStateRequestDefinition(clusterStateRequestBuilder: ClusterStateRequestBuilder) extends ActionRequest[ClusterStateResponse] {
    override def execute: (ActionListener[ClusterStateResponse]) => Unit = clusterStateRequestBuilder.execute
  }

  case object create {
    def repository(repositoryName: String) = {
      val putRepository: PutRepositoryRequestBuilder = clusterClient.preparePutRepository(repositoryName)
      PutRepositoryDefinition(putRepository)
    }

    def snapshot(snapshotName: String) = {
      CreateSnapshotDefinition(snapshotName)
    }

    def index(indexName: String) = CreateIndexDefinition(indicesClient.prepareCreate(indexName))
  }

  case class GetSettingsRequestDefinition(getSettingsRequestBuilder: GetSettingsRequestBuilder) extends ActionRequest[GetSettingsResponse] {
    override def execute: (ActionListener[GetSettingsResponse]) => Unit = getSettingsRequestBuilder.execute
  }

  case object get {
    def repository(repositoryName: String) = {
      val putRepository: PutRepositoryRequestBuilder = clusterClient.preparePutRepository(repositoryName)
      PutRepositoryDefinition(putRepository)
    }

    def snapshot(snapshotName: String) = {
      GetSnapshotDefinition(snapshotName)
    }

    def mapping(indexName: String) = {
      val mappingsRequestBuilder: GetMappingsRequestBuilder = indicesClient.prepareGetMappings(indexName)
      GetMappingDefinition(mappingsRequestBuilder)
    }

    def settings(indexName: String) = {
      val getSettingsRequestBuilder: GetSettingsRequestBuilder = indicesClient.prepareGetSettings(indexName)
      GetSettingsRequestDefinition(getSettingsRequestBuilder)
    }
  }

  case object delete {
    def snapshot(snapshotName: String) = {
      DeleteSnapshotDefinition(snapshotName)
    }
  }

  case class AddAliasRequestDefinition(targetAlias: String) extends ActionRequest[IndicesAliasesResponse] {
    var indicesAliasesRequestBuilder: IndicesAliasesRequestBuilder = null
    def on(sourceIndex: String) = {
      indicesAliasesRequestBuilder = indicesClient.prepareAliases().addAlias(sourceIndex, targetAlias)
      this
    }
    override def execute: (ActionListener[IndicesAliasesResponse]) => Unit = indicesAliasesRequestBuilder.execute
  }

  case object add {
    def alias(targetIndex: String) = {
      AddAliasRequestDefinition(targetIndex)
    }
  }

  case class RestoreSnapshotRequestDefinition(snapshotName: String) extends ActionRequest[RestoreSnapshotResponse] {
    var restoreSnapshotRequestBuilder: RestoreSnapshotRequestBuilder = null
    def from(repositoryName: String) = {
      restoreSnapshotRequestBuilder = clusterClient.prepareRestoreSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[RestoreSnapshotResponse]) => Unit = restoreSnapshotRequestBuilder.execute
  }

  case object restore {
    def snapshot(snapshotName: String) = RestoreSnapshotRequestDefinition(snapshotName)
  }

  case class CloseIndexRequestDefinition(closeIndexRequestBuilder: CloseIndexRequestBuilder) extends ActionRequest[CloseIndexResponse] {
    override def execute: (ActionListener[CloseIndexResponse]) => Unit = closeIndexRequestBuilder.execute
  }
  case object close {
    def index(indexName: String) = {
      val closeIndexRequestBuilder: CloseIndexRequestBuilder = indicesClient.prepareClose(indexName)
      CloseIndexRequestDefinition(closeIndexRequestBuilder)
    }
  }

  case class OpenIndexRequestDefinition(openIndexRequestBuilder: OpenIndexRequestBuilder) extends ActionRequest[OpenIndexResponse] {
    override def execute: (ActionListener[OpenIndexResponse]) => Unit = openIndexRequestBuilder.execute
  }

  case object open {
    def index(indexName: String) = {
      val openIndexRequestBuilder: OpenIndexRequestBuilder = indicesClient.prepareOpen(indexName)
      OpenIndexRequestDefinition(openIndexRequestBuilder)
    }
  }

  case class SearchRequestDefinition(searchRequestBuilder: SearchRequestBuilder) extends ActionRequest[SearchResponse] {
    def size(i: Int) = {
      searchRequestBuilder.setSize(i)
      this
    }
    override def execute: (ActionListener[SearchResponse]) => Unit = searchRequestBuilder.execute
  }

  case object search {
    def in(indexName: String) = {
      val searchRequestBuilder: SearchRequestBuilder = client.client.prepareSearch(indexName)
      SearchRequestDefinition(searchRequestBuilder)
    }
  }

  case class PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder) extends ActionRequest[PendingClusterTasksResponse] {
    override def execute: (ActionListener[PendingClusterTasksResponse]) => Unit = pendingClusterTasksRequestBuilder.execute
  }
  case object pending {
    def tasks = {
      val pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder = clusterClient.preparePendingClusterTasks()
      PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder)
    }
  }

  case object waiting {
    def index(indexName: String) = {
      val prepareHealth: ClusterHealthRequestBuilder = clusterClient.prepareHealth(indexName)
      ClusterHealthRequestDefinition(prepareHealth)
    }
  }
}

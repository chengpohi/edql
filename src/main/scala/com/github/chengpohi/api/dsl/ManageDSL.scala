package com.github.chengpohi.api.dsl

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequestBuilder
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksRequestBuilder
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder

/**
  * elasticshell
  * Created by chengpohi on 6/26/16.
  */
trait ManageDSL extends DSLDefinition with DeleterDSL with QueryDSL{
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

  case object indice {
    def stats(nodeIds: List[String]) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(nodeIds: _ *)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }
    def stats(indiceType: NodeType) = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(indiceType.value: _*)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }

    def update(indexName: String) = {
      val clusterUpdateSettingsRequestBuilder: UpdateSettingsRequestBuilder = indicesClient.prepareUpdateSettings(indexName)
      UpdateSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder)
    }
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


  case object add {
    def alias(targetIndex: String) = {
      AddAliasRequestDefinition(targetIndex)
    }
  }

  case object restore {
    def snapshot(snapshotName: String) = RestoreSnapshotRequestDefinition(snapshotName)
  }

  case object close {
    def index(indexName: String) = {
      val closeIndexRequestBuilder: CloseIndexRequestBuilder = indicesClient.prepareClose(indexName)
      CloseIndexRequestDefinition(closeIndexRequestBuilder)
    }
  }

  case object open {
    def index(indexName: String) = {
      val openIndexRequestBuilder: OpenIndexRequestBuilder = indicesClient.prepareOpen(indexName)
      OpenIndexRequestDefinition(openIndexRequestBuilder)
    }
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

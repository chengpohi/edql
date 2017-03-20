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
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequestBuilder, UpdateSettingsResponse}
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * elasticdsl
  * Created by chengpohi on 6/26/16.
  */
trait ManageDSL extends DSLDefinition with DeleterDSL with QueryDSL {

  case object node {
    def stats(nodeIds: List[String]): NodeStatsRequestDefinition = {
      val prepareNodesStats: NodesStatsRequestBuilder = clusterClient.prepareNodesStats(nodeIds: _*)
      NodeStatsRequestDefinition(prepareNodesStats)
    }

    def stats(nodeIds: NodeType): NodeStatsRequestDefinition = {
      val prepareNodesStats: NodesStatsRequestBuilder = clusterClient.prepareNodesStats()
      NodeStatsRequestDefinition(prepareNodesStats)
    }

    def info: NodeInfoRequestDefinition = {
      val prepareNodesStats: NodesInfoRequestBuilder = clusterClient.prepareNodesInfo()
      NodeInfoRequestDefinition(prepareNodesStats)
    }
  }

  case object indice {
    def stats(nodeIds: List[String]): IndicesStatsRequestDefinition = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(nodeIds: _ *)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }

    def stats(indiceType: NodeType): IndicesStatsRequestDefinition = {
      val prepareNodesStats: IndicesStatsRequestBuilder = indicesClient.prepareStats(indiceType.value: _*)
      IndicesStatsRequestDefinition(prepareNodesStats)
    }

    def update(indexName: String): UpdateSettingsRequestDefinition = {
      val clusterUpdateSettingsRequestBuilder: UpdateSettingsRequestBuilder = indicesClient.prepareUpdateSettings(indexName)
      UpdateSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder)
    }
  }

  case object cluster {
    def stats: ClusterStatsRequestDefinition = {
      val prepareNodesStats: ClusterStatsRequestBuilder = clusterClient.prepareClusterStats()
      ClusterStatsRequestDefinition(prepareNodesStats)
    }

    def state: ClusterStateRequestDefinition = {
      val stateRequestBuilder: ClusterStateRequestBuilder = clusterClient.prepareState()
      ClusterStateRequestDefinition(stateRequestBuilder)
    }

    def health: ClusterHealthRequestDefinition = {
      val clusterHealthRequestBuilder: ClusterHealthRequestBuilder = clusterClient.prepareHealth()
      ClusterHealthRequestDefinition(clusterHealthRequestBuilder)
    }

    def settings: ClusterSettingsRequestDefinition = {
      val clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder = clusterClient.prepareUpdateSettings()
      ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder)
    }
  }

  case object create {
    def repository(repositoryName: String): PutRepositoryDefinition = {
      val putRepository: PutRepositoryRequestBuilder = clusterClient.preparePutRepository(repositoryName)
      PutRepositoryDefinition(putRepository)
    }

    def snapshot(snapshotName: String): CreateSnapshotDefinition = {
      CreateSnapshotDefinition(snapshotName)
    }

    def index(indexName: String): CreateIndexDefinition = CreateIndexDefinition(indicesClient.prepareCreate(indexName))

    def analyzer(analyzer: String): CreateAnalyzerRequestDefinition =
      CreateAnalyzerRequestDefinition(analyzer)
  }


  case object add {
    def alias(targetIndex: String): AddAliasRequestDefinition = {
      AddAliasRequestDefinition(targetIndex)
    }
  }

  case object restore {
    def snapshot(snapshotName: String): RestoreSnapshotRequestDefinition = RestoreSnapshotRequestDefinition(snapshotName)
  }

  case object close {
    def index(indexName: String): CloseIndexRequestDefinition = {
      val closeIndexRequestBuilder: CloseIndexRequestBuilder = indicesClient.prepareClose(indexName)
      CloseIndexRequestDefinition(closeIndexRequestBuilder)
    }
  }

  case object open {
    def index(indexName: String): OpenIndexRequestDefinition = {
      val openIndexRequestBuilder: OpenIndexRequestBuilder = indicesClient.prepareOpen(indexName)
      OpenIndexRequestDefinition(openIndexRequestBuilder)
    }
  }


  case object pending {
    def tasks: PendingClusterTasksDefinition = {
      val pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder = clusterClient.preparePendingClusterTasks()
      PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder)
    }
  }

  case object waiting {
    def index(indexName: String): ClusterHealthRequestDefinition = {
      val prepareHealth: ClusterHealthRequestBuilder = clusterClient.prepareHealth(indexName)
      ClusterHealthRequestDefinition(prepareHealth)
    }
  }

  case object refresh {
    def index(indices: String): RefreshRequestDefinition = {
      RefreshRequestDefinition(indices)
    }
  }

  case class CreateAnalyzerRequestDefinition(analyzerSetting: String) extends Definition[UpdateSettingsResponse] {
    override def execute: Future[UpdateSettingsResponse] = {
      val p = Promise[UpdateSettingsResponse]()
      DSL {
        close index ELASTIC_SHELL_INDEX_NAME
      } onSuccess {
        case _ =>
          val eventualUpdateSettingsResponse: Future[UpdateSettingsResponse] = DSL {
            indice update ELASTIC_SHELL_INDEX_NAME settings analyzerSetting
          }
          eventualUpdateSettingsResponse.onSuccess {
            case r => p success r
          }
      }
      p.future onSuccess {
        case _ => DSL {
          open index ELASTIC_SHELL_INDEX_NAME
        }
      }
      p.future
    }

    override def json: String = execute.await.toJson
  }

}

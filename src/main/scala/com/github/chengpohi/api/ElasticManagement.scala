package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.ManageDSL
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.cluster.health.ClusterHealthStatus

import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticManagement extends ManageDSL {
  def nodeStats: Future[NodesStatsResponse] = ElasticExecutor {
    node stats NodeType.ALL flag FlagType.ALL
  }

  def indicesStats: Future[IndicesStatsResponse] = ElasticExecutor {
    indice stats NodeType.ALL flag FlagType.ALL
  }

  def clusterStats: Future[ClusterStatsResponse] = ElasticExecutor {
    cluster stats
  }

  def createRepository(repositoryName: String, repositoryType: String, st: Map[String, AnyRef]): Future[PutRepositoryResponse] = ElasticExecutor {
    create repository repositoryName `type` repositoryType settings st
  }

  def createSnapshot(snapshotName: String, repositoryName: String): Future[CreateSnapshotResponse] = ElasticExecutor {
    create snapshot snapshotName in repositoryName
  }

  def getSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[GetSnapshotsResponse] = ElasticExecutor {
    get snapshot snapshotName from repositoryName
  }

  def getAllSnapshotByRepositoryName(repositoryName: String): Future[GetSnapshotsResponse] = ElasticExecutor {
    get snapshot "*" from repositoryName
  }

  def deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[DeleteSnapshotResponse] = ElasticExecutor {
    delete snapshot snapshotName from repositoryName
  }

  def mappings(indexName: String, mapping: String): Future[CreateIndexResponse] = ElasticExecutor {
    create index indexName mappings mapping
  }

  def getMapping(indexName: String) = ElasticExecutor {
    get mapping indexName
  }

  def getIndices: Future[ClusterStateResponse] = ElasticExecutor {
    cluster state
  }

  def clusterHealth = ElasticExecutor {
    cluster health
  }

  def alias(targetIndex: String, sourceIndex: String) = ElasticExecutor {
    add alias targetIndex on sourceIndex
  }

  def restoreSnapshot(snapshotName: String, repositoryName: String) = ElasticExecutor {
    restore snapshot snapshotName from repositoryName
  }

  def closeIndex(indexName: String) = ElasticExecutor {
    close index indexName
  }

  def openIndex(indexName: String) = ElasticExecutor {
    open index indexName
  }

  def countCommand(indexName: String): Future[SearchResponse] = ElasticExecutor {
    search in indexName size 0
  }

  def clusterSettings: Future[ClusterUpdateSettingsResponse] = ElasticExecutor {
    cluster settings
  }

  def nodesSettings: Future[NodesInfoResponse] = ElasticExecutor {
    node info
  }

  def indexSettings(indexName: String) = ElasticExecutor {
    get settings indexName
  }

  def pendingTasks: Future[PendingClusterTasksResponse] = ElasticExecutor {
    pending tasks
  }

  def waitForStatus(indexName: Option[String] = Some("*"), status: Option[String] = Some("GREEN"), timeOut: Option[String] = Some("100s")): Future[ClusterHealthResponse] = {
    val clusterHealthStatus: ClusterHealthStatus = status match {
      case Some("GREEN") => ClusterHealthStatus.GREEN
      case Some("RED") => ClusterHealthStatus.RED
      case Some("YELLOW") => ClusterHealthStatus.YELLOW
      case _ => ClusterHealthStatus.GREEN
    }

    ElasticExecutor {
      waiting index indexName.get timeout timeOut.get status clusterHealthStatus
    }
  }
}

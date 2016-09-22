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
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.cluster.health.ClusterHealthStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticManagement extends ManageDSL {

  import DSLHelper._

  def nodeStats: Future[NodesStatsResponse] = DSL {
    node stats NodeType.ALL flag FlagType.ALL
  }

  def indicesStats: Future[IndicesStatsResponse] = DSL {
    indice stats NodeType.ALL flag FlagType.ALL
  }

  def clusterStats: Future[ClusterStatsResponse] = DSL {
    cluster stats
  }

  def createRepository(repositoryName: String, repositoryType: String, st: Map[String, AnyRef]): Future[PutRepositoryResponse] = DSL {
    create repository repositoryName tpe repositoryType settings st
  }

  def createSnapshot(snapshotName: String, repositoryName: String): Future[CreateSnapshotResponse] = DSL {
    create snapshot snapshotName in repositoryName
  }

  def getSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[GetSnapshotsResponse] = DSL {
    get snapshot snapshotName from repositoryName
  }

  def getAllSnapshotByRepositoryName(repositoryName: String): Future[GetSnapshotsResponse] = DSL {
    get snapshot "*" from repositoryName
  }

  def deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[DeleteSnapshotResponse] = DSL {
    delete snapshot snapshotName from repositoryName
  }

  def mappings(indexName: String, mapping: String): Future[CreateIndexResponse] = DSL {
    create index indexName mappings mapping
  }

  def updateMappings(indexName: String, indexType: String, mapping: String): Future[PutMappingResponse] = DSL {
    update index indexName / indexType mapping mapping
  }

  def getMapping(indexName: String): Future[GetMappingsResponse] = DSL {
    get mapping indexName
  }

  def getIndices: Future[ClusterStateResponse] = DSL {
    cluster state
  }

  def clusterHealth: Future[ClusterHealthResponse] = DSL {
    cluster health
  }

  def shutdown: Future[String] = Future {
    client.close()
    "shutdown"
  }

  def alias(targetIndex: String, sourceIndex: String): Future[IndicesAliasesResponse] = DSL {
    add alias targetIndex on sourceIndex
  }

  def restoreSnapshot(snapshotName: String, repositoryName: String): Future[RestoreSnapshotResponse] = DSL {
    restore snapshot snapshotName from repositoryName
  }

  def closeIndex(indexName: String): Future[CloseIndexResponse] = DSL {
    close index indexName
  }

  def openIndex(indexName: String): Future[OpenIndexResponse] = DSL {
    open index indexName
  }

  def countCommand(indexName: String): Future[SearchResponse] = DSL {
    search in indexName size 0
  }

  def clusterSettings: Future[ClusterUpdateSettingsResponse] = DSL {
    cluster settings
  }

  def nodesSettings: Future[NodesInfoResponse] = DSL {
    node info
  }

  def indexSettings(indexName: String): Future[GetSettingsResponse] = DSL {
    get settings indexName
  }

  def pendingTasks: Future[PendingClusterTasksResponse] = DSL {
    pending tasks
  }

  def createIndex(indexName: String): Future[CreateIndexResponse] = DSL {
    create index indexName
  }

  def waitForStatus(indexName: Option[String] = Some("*"),
                    status: Option[String] = Some("GREEN"),
                    timeOut: Option[String] = Some("100s")): Future[ClusterHealthResponse] = {
    val clusterHealthStatus: ClusterHealthStatus = status match {
      case Some("GREEN") => ClusterHealthStatus.GREEN
      case Some("RED") => ClusterHealthStatus.RED
      case Some("YELLOW") => ClusterHealthStatus.YELLOW
      case _ => ClusterHealthStatus.GREEN
    }

    DSL {
      waiting index indexName.get timeout timeOut.get status clusterHealthStatus
    }
  }
}

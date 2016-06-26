package com.github.chengpohi.api

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.RichSearchResponse
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
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.settings.Settings

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticManagement {
  this: ElasticBase =>
  def nodeStats: Future[NodesStatsResponse] = ActionFuture {
    cluster prepareNodesStats() all() execute
  }

  def indicesStats: Future[IndicesStatsResponse] = ActionFuture {
    client.admin.indices().prepareStats().all().execute
  }

  def clusterStats: Future[ClusterStatsResponse] = ActionFuture {
    client.client.admin().cluster().prepareClusterStats().execute
  }

  def createRepository(repositoryName: String, repositoryType: String, st: Map[String, AnyRef]): Future[PutRepositoryResponse] = {
    ActionFuture {
      client.client.admin().cluster().preparePutRepository(repositoryName)
        .setType(repositoryType)
        .setSettings(st.asJava)
        .execute
    }
  }

  def createSnapshot(snapshotName: String, repositoryName: String): Future[CreateSnapshotResponse] = ActionFuture {
    client.client.admin().cluster().prepareCreateSnapshot(repositoryName, snapshotName).execute
  }

  def getSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[GetSnapshotsResponse] = ActionFuture {
    client.client.admin().cluster().prepareGetSnapshots(repositoryName).setSnapshots(snapshotName).execute
  }

  def getAllSnapshotByRepositoryName(repositoryName: String): Future[GetSnapshotsResponse] = ActionFuture {
    client.client.admin().cluster().prepareGetSnapshots(repositoryName).execute
  }

  def deleteSnapshotBySnapshotNameAndRepositoryName(snapshotName: String, repositoryName: String): Future[DeleteSnapshotResponse] = ActionFuture {
    client.client.admin().cluster().prepareDeleteSnapshot(repositoryName, snapshotName).execute
  }

  def mappings(indexName: String, mapping: String): Future[CreateIndexResponse] = ActionFuture {
    client.client.admin().indices().prepareCreate(indexName).setSource(mapping).execute
  }

  def getMapping(indexName: String) = client.execute {
    get mapping indexName
  }

  def getIndices: Future[ClusterStateResponse] = ActionFuture {
    client.admin.cluster().prepareState().execute
  }

  def clusterHealth = client.execute {
    get cluster health
  }

  def alias(targetIndex: String, sourceIndex: String) = {
    client.execute {
      add alias targetIndex on sourceIndex
    }
  }


  def restoreSnapshot(snapshotName: String, repositoryName: String) = {
    client.execute {
      restore snapshot snapshotName from repositoryName
    }
  }

  def closeIndex(indexName: String) = client.execute {
    close index indexName
  }

  def openIndex(indexName: String) = {
    client.execute {
      open index indexName
    }
  }

  def countCommand(indexName: String): Future[RichSearchResponse] = client.execute {
    search in indexName size 0
  }

  def clusterSettings: Future[ClusterUpdateSettingsResponse] = ActionFuture {
    client.admin.cluster().prepareUpdateSettings().execute
  }


  def nodesSettings: Future[NodesInfoResponse] = ActionFuture {
    client.admin.cluster().prepareNodesInfo().execute
  }

  def indexSettings(indexName: String) = client.execute {
    get settings indexName
  }

  def pendingTasks: Future[PendingClusterTasksResponse] = ActionFuture {
    client.admin.cluster().preparePendingClusterTasks().execute
  }

  def waitForStatus(indexName: Option[String] = Some("*"), status: Option[String] = Some("GREEN"), timeOut: Option[String] = Some("100s")): Future[ClusterHealthResponse] = {
    val clusterHealthStatus: ClusterHealthStatus = status match {
      case Some("GREEN") => ClusterHealthStatus.GREEN
      case Some("RED") => ClusterHealthStatus.RED
      case Some("YELLOW") => ClusterHealthStatus.YELLOW
      case _ => ClusterHealthStatus.GREEN
    }
    ActionFuture {
      client.admin.cluster()
        .prepareHealth(indexName.get)
        .setTimeout(timeOut.get)
        .setWaitForStatus(clusterHealthStatus).execute
    }
  }
}

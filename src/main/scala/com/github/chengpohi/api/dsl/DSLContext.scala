package com.github.chengpohi.api.dsl

import com.github.chengpohi.collection.JsonCollection.Val
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
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse
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
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.update.UpdateResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by xiachen on 10/11/2016.
  */

trait DSLContext extends DSLDefinition {
  val responseGenerator = new ResponseGenerator

  trait Monoid[A] {
    def toJson(a: A): String
  }

  object Monoid {

    implicit object IndexResponseMonoid extends Monoid[IndexResponse] {
      override def toJson(a: IndexResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object DeleteIndexResponseMonoid extends Monoid[DeleteIndexResponse] {
      override def toJson(a: DeleteIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object CreateIndexResponseMonoid extends Monoid[CreateIndexResponse] {
      override def toJson(a: CreateIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object SearchResponseMonoid extends Monoid[SearchResponse] {
      override def toJson(a: SearchResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object NodeStatsResponseMonoid extends Monoid[NodesStatsResponse] {
      override def toJson(a: NodesStatsResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object NodeInfoResponseMonoid extends Monoid[NodesInfoResponse] {
      override def toJson(a: NodesInfoResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object IndicesStatsResponseMonoid extends Monoid[IndicesStatsResponse] {
      override def toJson(a: IndicesStatsResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object ClusterStatsResponseMonoid extends Monoid[ClusterStatsResponse] {
      override def toJson(a: ClusterStatsResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object ClusterHealthResponseMonoid extends Monoid[ClusterHealthResponse] {
      override def toJson(a: ClusterHealthResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object ClusterUpdateSettingsResponseMonoid extends Monoid[ClusterUpdateSettingsResponse] {
      override def toJson(a: ClusterUpdateSettingsResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object UpdateSettingsResponseMonoid extends Monoid[UpdateSettingsResponse] {
      override def toJson(a: UpdateSettingsResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object PutRepositoryResponseMonoid extends Monoid[PutRepositoryResponse] {
      override def toJson(a: PutRepositoryResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object CreateSnapshotResponseMonoid extends Monoid[CreateSnapshotResponse] {
      override def toJson(a: CreateSnapshotResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object GetSnapshotsResponseMonoid extends Monoid[GetSnapshotsResponse] {
      override def toJson(a: GetSnapshotsResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object DeleteSnapshotResponseMonoid extends Monoid[DeleteSnapshotResponse] {
      override def toJson(a: DeleteSnapshotResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object GetMappingsResponseMonoid extends Monoid[GetMappingsResponse] {
      override def toJson(a: GetMappingsResponse): String = responseGenerator.buildGetMappingResponse(a)
    }

    implicit object PutMappingResponseMonoid extends Monoid[PutMappingResponse] {
      override def toJson(a: PutMappingResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object ClusterStateResponseMonoid extends Monoid[ClusterStateResponse] {
      override def toJson(a: ClusterStateResponse): String = responseGenerator.buildXContent(a.getState)
    }

    implicit object GetSettingsResponseMonoid extends Monoid[GetSettingsResponse] {
      override def toJson(a: GetSettingsResponse): String = responseGenerator.buildGetSettingsResponse(a)
    }

    implicit object IndicesAliasesResponseMonoid extends Monoid[IndicesAliasesResponse] {
      override def toJson(a: IndicesAliasesResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object RestoreSnapshotResponseMonoid extends Monoid[RestoreSnapshotResponse] {
      override def toJson(a: RestoreSnapshotResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object CloseIndexResponseMonoid extends Monoid[CloseIndexResponse] {
      override def toJson(a: CloseIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object PendingClusterTasksResponseMonoid extends Monoid[PendingClusterTasksResponse] {
      override def toJson(a: PendingClusterTasksResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object OpenIndexResponseMonoid extends Monoid[OpenIndexResponse] {
      override def toJson(a: OpenIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)
    }

    implicit object AnalyzeResponseMonoid extends Monoid[AnalyzeResponse] {
      override def toJson(a: AnalyzeResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object DeleteResponseMonoid extends Monoid[DeleteResponse] {
      override def toJson(a: DeleteResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object UpdateResponseMonoid extends Monoid[UpdateResponse] {
      override def toJson(a: UpdateResponse): String = responseGenerator.buildXContent(a)
    }

    implicit object GetResponseMonoid extends Monoid[GetResponse] {
      override def toJson(a: GetResponse): String = responseGenerator.buildXContent(a)
    }

  }

  trait MonoidOp[A] {
    val F: Monoid[A]
    val value: A

    def toJson: String = F.toJson(value)
  }

  implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
    val F = implicitly[Monoid[A]]
    val value = a
  }


  trait FutureToJson[A, Future[_]] {
    val F: Monoid[A]
    val value: A

    def toJson: String = F.toJson(value)

    def await: A = value
  }

  implicit def futureToJson[A: Monoid](a: Future[A]): FutureToJson[A, Future] = new FutureToJson[A, Future] {
    override val F: Monoid[A] = implicitly[Monoid[A]]
    override val value: A = Await.result(a, Duration.Inf)
  }

  implicit class IndexNameAndIndexType(indexName: String) {
    def /(indexType: String): IndexPath = {
      IndexPath(indexName, indexType)
    }
  }

  implicit class IndexNameAndIndexTypeVal(indexName: Val) {
    def /(indexType: Val): IndexPath = {
      IndexPath(indexName.extract[String], indexType.extract[String])
    }
  }

}


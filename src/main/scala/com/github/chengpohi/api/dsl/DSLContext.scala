package com.github.chengpohi.api.dsl

import com.github.chengpohi.collection.JsonCollection.Val
import com.github.chengpohi.helper.ResponseGenerator
import org.elasticsearch.action.ListenableActionFuture
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
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.termvectors.TermVectorsResponse
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders, RangeQueryBuilder}
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilder, SortOrder}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  * Created by xiachen on 10/11/2016.
  */
trait DSLContext extends DSLExecutor {
  val responseGenerator = new ResponseGenerator

  trait Monoid[A] {
    def toJson(a: A): String

    def as[T](a: A)(implicit mf: Manifest[T]): Stream[T]
  }

  object Monoid {

    implicit object StringResponseMonoid extends Monoid[String] {
      override def toJson(a: String): String = a

      override def as[T](a: String)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object IndexResponseMonoid extends Monoid[IndexResponse] {
      override def toJson(a: IndexResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: IndexResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object BulkResponseMonoid extends Monoid[BulkResponse] {
      override def toJson(a: BulkResponse): String = responseGenerator.buildBulkResponse(a)

      override def as[T](a: BulkResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object DeleteIndexResponseMonoid extends Monoid[DeleteIndexResponse] {
      override def toJson(a: DeleteIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: DeleteIndexResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object CreateIndexResponseMonoid extends Monoid[CreateIndexResponse] {
      override def toJson(a: CreateIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: CreateIndexResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object SearchResponseMonoid extends Monoid[SearchResponse] {
      override def toJson(a: SearchResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: SearchResponse)(implicit mf: Manifest[T]): Stream[T] = {
        a.getHits.getHits.toStream.map(t => {
          val source = t.getSource.asScala.toMap + ("id" -> t.getId)
          responseGenerator.extractObjectByMap(source)(mf)
        })
      }
    }

    implicit object NodeStatsResponseMonoid extends Monoid[NodesStatsResponse] {
      override def toJson(a: NodesStatsResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: NodesStatsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object NodeInfoResponseMonoid extends Monoid[NodesInfoResponse] {
      override def toJson(a: NodesInfoResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: NodesInfoResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object IndicesStatsResponseMonoid extends Monoid[IndicesStatsResponse] {
      override def toJson(a: IndicesStatsResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: IndicesStatsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object ClusterStatsResponseMonoid extends Monoid[ClusterStatsResponse] {
      override def toJson(a: ClusterStatsResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: ClusterStatsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object ClusterHealthResponseMonoid extends Monoid[ClusterHealthResponse] {
      override def toJson(a: ClusterHealthResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: ClusterHealthResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object ClusterUpdateSettingsResponseMonoid extends Monoid[ClusterUpdateSettingsResponse] {
      override def toJson(a: ClusterUpdateSettingsResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: ClusterUpdateSettingsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object UpdateSettingsResponseMonoid extends Monoid[UpdateSettingsResponse] {
      override def toJson(a: UpdateSettingsResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: UpdateSettingsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object PutRepositoryResponseMonoid extends Monoid[PutRepositoryResponse] {
      override def toJson(a: PutRepositoryResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: PutRepositoryResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object CreateSnapshotResponseMonoid extends Monoid[CreateSnapshotResponse] {
      override def toJson(a: CreateSnapshotResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: CreateSnapshotResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object GetSnapshotsResponseMonoid extends Monoid[GetSnapshotsResponse] {
      override def toJson(a: GetSnapshotsResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: GetSnapshotsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object DeleteSnapshotResponseMonoid extends Monoid[DeleteSnapshotResponse] {
      override def toJson(a: DeleteSnapshotResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: DeleteSnapshotResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object GetMappingsResponseMonoid extends Monoid[GetMappingsResponse] {
      override def toJson(a: GetMappingsResponse): String = responseGenerator.buildGetMappingResponse(a)

      override def as[T](a: GetMappingsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object PutMappingResponseMonoid extends Monoid[PutMappingResponse] {
      override def toJson(a: PutMappingResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: PutMappingResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object ClusterStateResponseMonoid extends Monoid[ClusterStateResponse] {
      override def toJson(a: ClusterStateResponse): String = responseGenerator.buildXContent(a.getState)

      override def as[T](a: ClusterStateResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object GetSettingsResponseMonoid extends Monoid[GetSettingsResponse] {
      override def toJson(a: GetSettingsResponse): String = responseGenerator.buildGetSettingsResponse(a)

      override def as[T](a: GetSettingsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object IndicesAliasesResponseMonoid extends Monoid[IndicesAliasesResponse] {
      override def toJson(a: IndicesAliasesResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: IndicesAliasesResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object RestoreSnapshotResponseMonoid extends Monoid[RestoreSnapshotResponse] {
      override def toJson(a: RestoreSnapshotResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: RestoreSnapshotResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object CloseIndexResponseMonoid extends Monoid[CloseIndexResponse] {
      override def toJson(a: CloseIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: CloseIndexResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object PendingClusterTasksResponseMonoid extends Monoid[PendingClusterTasksResponse] {
      override def toJson(a: PendingClusterTasksResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: PendingClusterTasksResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object OpenIndexResponseMonoid extends Monoid[OpenIndexResponse] {
      override def toJson(a: OpenIndexResponse): String = responseGenerator.buildAcknowledgedResponse(a)

      override def as[T](a: OpenIndexResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object AnalyzeResponseMonoid extends Monoid[AnalyzeResponse] {
      override def toJson(a: AnalyzeResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: AnalyzeResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object DeleteResponseMonoid extends Monoid[DeleteResponse] {
      override def toJson(a: DeleteResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: DeleteResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object UpdateResponseMonoid extends Monoid[UpdateResponse] {
      override def toJson(a: UpdateResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: UpdateResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object GetResponseMonoid extends Monoid[GetResponse] {
      override def toJson(a: GetResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: GetResponse)(implicit typeTag: Manifest[T]): Stream[T] = {
        a.isExists match {
          case true =>
            val source: Map[String, AnyRef] = a.getSource.asScala.toMap + ("id" -> a.getId)
            Stream(responseGenerator.extractObjectByMap(source))
          case false => Stream.empty
        }
      }
    }

    implicit object StreamSearchResponseMonoid extends Monoid[Stream[SearchResponse]] {
      override def toJson(a: Stream[SearchResponse]): String = {
        val s = a.map(s => s.toJson)
        responseGenerator.buildStream(s)
      }

      override def as[T](a: Stream[SearchResponse])(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object JoinSearchResponseMonoid extends Monoid[Stream[Map[String, AnyRef]]] {
      override def toJson(a: Stream[Map[String, AnyRef]]): String = {
        responseGenerator.buildStreamMapTupels(a)
      }

      override def as[T](a: Stream[Map[String, AnyRef]])(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object SearchHitMonoid extends Monoid[SearchHit] {
      override def toJson(a: SearchHit): String = responseGenerator.buildXContent(a)

      override def as[T](a: SearchHit)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object RefreshResponseMonoid extends Monoid[RefreshResponse] {
      override def toJson(a: RefreshResponse): String = a.toString

      override def as[T](a: RefreshResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object TermVectorResponseMonoid extends Monoid[TermVectorsResponse] {
      override def toJson(a: TermVectorsResponse): String = responseGenerator.buildXContent(a)

      override def as[T](a: TermVectorsResponse)(implicit mf: Manifest[T]): Stream[T] = Stream.empty
    }

    implicit object StreamSearchHitMonoid extends Monoid[Stream[SearchHit]] {
      override def toJson(a: Stream[SearchHit]): String = {
        val s = a.map(s => s.toJson)
        responseGenerator.buildStream(s)
      }

      override def as[T](a: Stream[SearchHit])(implicit mf: Manifest[T]): Stream[T] = {
        a.map(t => {
          val source: Map[String, AnyRef] = t.getSource.asScala.toMap + ("id" -> t.getId)
          responseGenerator.extractObjectByMap(source)
        })
      }
    }

  }

  trait MonoidOp[A] {
    val F: Monoid[A]
    val value: A

    def toJson: String = F.toJson(value)

    def as[T](implicit mf: Manifest[T]): Stream[T] = F.as(value)
  }

  implicit def toMonoidOp[A: Monoid](a: A): MonoidOp[A] = new MonoidOp[A] {
    val F = implicitly[Monoid[A]]
    val value = a
  }


  trait FutureToJson[A] {
    val F: Monoid[A]
    val value: Future[A]

    def toJson: String = {
      val result = Await.result(value, Duration.Inf)
      F.toJson(result)
    }

    def await: A = Await.result(value, Duration.Inf)

    def as[T](implicit mf: Manifest[T]): Future[Stream[T]] = value.map(a => {
      F.as(a)
    })
  }

  implicit def futureToJson[A: Monoid](a: Future[A]): FutureToJson[A] = new FutureToJson[A] {
    override val F: Monoid[A] = implicitly[Monoid[A]]
    override val value: Future[A] = a
  }

  implicit def listenableActionFutureMonoid[A: Monoid](a: ListenableActionFuture[A]): FutureToJson[A] = new FutureToJson[A] {
    override val F: Monoid[A] = implicitly[Monoid[A]]
    override val value: Future[A] = a
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

    def /(indexType: String): IndexPath = {
      IndexPath(indexName.extract[String], indexType)
    }
  }

  case class IndexPath(indexName: String, indexType: String)

  implicit class StringBuilders(fieldName: String) {
    def gt(v: String): RangeQueryBuilder = {
      new RangeQueryBuilder(fieldName).gt(v)
    }

    def as(o: SortOrder): SortBuilder[_] = {
      new FieldSortBuilder(fieldName).order(o)
    }
  }

  implicit def strToQueryBuilder(query: String): QueryBuilder = query match {
    case "*" => {
      QueryBuilders.matchAllQuery()
    }
    case _ => QueryBuilders.queryStringQuery(query)
  }

}

trait Definition[A] {
  def execute: Future[A]

  def extract(path: String): ExtractDefinition = {
    ExtractDefinition(this, path)
  }

  def json: String
}

case class ExtractDefinition(definition: Definition[_], path: String) extends Definition[String] {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val formats = DefaultFormats

  override def execute: Future[String] = {
    Future {
      val jObj = parse(definition.json)
      val result = path.split("\\.").foldLeft(jObj) { (o, i) => o \ i }
      write(result)
    }
  }

  override def json: String = Await.result(execute, Duration.Inf)
}

package com.github.chengpohi.api.eql

import java.io.Serializable

import com.github.chengpohi.annotation.{Alias, Analyzer, CopyTo, Index}
import com.github.chengpohi.api.ElasticBase
import com.github.chengpohi.collection.JsonCollection.Val
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
import org.elasticsearch.action.admin.indices.analyze.{AnalyzeRequestBuilder, AnalyzeResponse}
import org.elasticsearch.action.admin.indices.close.{CloseIndexRequestBuilder, CloseIndexResponse}
import org.elasticsearch.action.admin.indices.create.{CreateIndexRequestBuilder, CreateIndexResponse}
import org.elasticsearch.action.admin.indices.delete.{DeleteIndexRequestBuilder, DeleteIndexResponse}
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
import org.elasticsearch.action.admin.indices.mapping.get.{GetMappingsRequestBuilder, GetMappingsResponse}
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.action.admin.indices.open.{OpenIndexRequestBuilder, OpenIndexResponse}
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequestBuilder, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequestBuilder, UpdateSettingsResponse}
import org.elasticsearch.action.admin.indices.stats.{IndicesStatsRequestBuilder, IndicesStatsResponse}
import org.elasticsearch.action.bulk.{BulkRequestBuilder, BulkResponse}
import org.elasticsearch.action.delete.{DeleteRequestBuilder, DeleteResponse}
import org.elasticsearch.action.get.{GetRequestBuilder, GetResponse}
import org.elasticsearch.action.index.{IndexRequestBuilder, IndexResponse}
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse, SearchScrollRequestBuilder, SearchType}
import org.elasticsearch.action.update.{UpdateRequestBuilder, UpdateResponse}
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query._
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramAggregationBuilder, DateHistogramInterval}
import org.elasticsearch.search.sort.SortBuilder
import scalaz.Scalaz._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Future
import scala.reflect.runtime.universe

/**
  * eql
  * Created by chengpohi on 6/28/16.
  */
trait EQLDefinition extends ElasticBase with EQLContext {
  val ELASTIC_SHELL_INDEX_NAME: String = ".eql"
  val DEFAULT_RETRIEVE_SIZE: Int = 500
  val MAX_ALL_NUMBER: Int = 10000
  val MAX_RETRIEVE_SIZE: Int = 500

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

  trait AttrType

  case object id extends AttrType

  case class NodeStatsRequestDefinition(
      nodesStatsRequestBuilder: NodesStatsRequestBuilder)
      extends Definition[NodesStatsResponse] {
    def flag(f: FlagType): NodeStatsRequestDefinition = {
      nodesStatsRequestBuilder.all()
      this
    }

    override def execute: Future[NodesStatsResponse] = {
      nodesStatsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class NodeInfoRequestDefinition(
      nodesInfoRequestBuilder: NodesInfoRequestBuilder)
      extends Definition[NodesInfoResponse] {
    override def execute: Future[NodesInfoResponse] = {
      nodesInfoRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class IndicesStatsRequestDefinition(
      indicesStatsRequestBuilder: IndicesStatsRequestBuilder)
      extends Definition[IndicesStatsResponse] {
    def flag(f: FlagType): IndicesStatsRequestDefinition = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: Future[IndicesStatsResponse] = {
      indicesStatsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ClusterStatsRequestDefinition(
      clusterStatsRequestBuilder: ClusterStatsRequestBuilder)
      extends Definition[ClusterStatsResponse] {
    override def execute: Future[ClusterStatsResponse] = {
      clusterStatsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ClusterHealthRequestDefinition(
      clusterHealthRequestBuilder: ClusterHealthRequestBuilder)
      extends Definition[ClusterHealthResponse] {
    def timeout(time: String): ClusterHealthRequestDefinition = {
      clusterHealthRequestBuilder.setTimeout(time)
      this
    }

    def status(status: String): ClusterHealthRequestDefinition = {
      val clusterHealthStatus: ClusterHealthStatus = status match {
        case "GREEN"  => ClusterHealthStatus.GREEN
        case "RED"    => ClusterHealthStatus.RED
        case "YELLOW" => ClusterHealthStatus.YELLOW
        case _        => ClusterHealthStatus.GREEN
      }
      clusterHealthRequestBuilder.setWaitForStatus(clusterHealthStatus)
      this
    }

    override def execute: Future[ClusterHealthResponse] = {
      clusterHealthRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ClusterSettingsRequestDefinition(
      clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder)
      extends Definition[ClusterUpdateSettingsResponse] {
    override def execute: Future[ClusterUpdateSettingsResponse] = {
      clusterUpdateSettingsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class UpdateSettingsRequestDefinition(
      updateSettingsRequestBuilder: UpdateSettingsRequestBuilder)
      extends Definition[UpdateSettingsResponse] {
    def settings(st: String): UpdateSettingsRequestDefinition = {
      updateSettingsRequestBuilder.setSettings(st, XContentType.JSON)
      this
    }

    override def execute: Future[UpdateSettingsResponse] = {
      updateSettingsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class PutRepositoryDefinition(
      putRepositoryRequestBuilder: PutRepositoryRequestBuilder)
      extends Definition[PutRepositoryResponse] {
    def tpe(`type`: String): PutRepositoryDefinition = {
      putRepositoryRequestBuilder.setType(`type`)
      this
    }

    def settings(st: Map[String, AnyRef]): PutRepositoryDefinition = {
      putRepositoryRequestBuilder.setSettings(st.asJava)
      this
    }

    override def execute: Future[PutRepositoryResponse] = {
      putRepositoryRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class CreateSnapshotDefinition(snapshotName: String)
      extends Definition[CreateSnapshotResponse] {
    var createSnapshotRequestBuilder: CreateSnapshotRequestBuilder = _

    def in(repositoryName: String): CreateSnapshotDefinition = {
      createSnapshotRequestBuilder =
        clusterClient.prepareCreateSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[CreateSnapshotResponse] = {
      createSnapshotRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class CreateIndexDefinition(
      createIndexRequestBuilder: CreateIndexRequestBuilder)
      extends Definition[CreateIndexResponse] {
    var _analyzers = List[AnalyzerDefinition]()
    var _tokenizers = List[NgramTokenizerDefinition]()
    var _fields = List[FieldDefinition]()
    var _settings: Option[IndexSettingsDefinition] = None
    var _s: Option[IndexSettings] = None
    var _m: Option[MappingsDefinition] = None
    var _status: Option[OperationStatus] = None

    def settings(settings: IndexSettingsDefinition): CreateIndexDefinition = {
      _settings = Some(settings)
      this
    }
    def settings(settings: IndexSettings): CreateIndexDefinition = {
      this._s = Some(settings)
      this
    }

    def mappings(m: String): CreateIndexDefinition = {
      createIndexRequestBuilder.setSource(m, XContentType.JSON)
      this
    }

    def mappings(m: MappingsDefinition): CreateIndexDefinition = {
      this._m = Some(m)
      this
    }

    def tokenizer(m: List[NgramTokenizerDefinition]): CreateIndexDefinition = {
      _tokenizers = m
      this
    }

    def analyzers(m: List[AnalyzerDefinition]): CreateIndexDefinition = {
      _analyzers = m
      this
    }

    def fields(m: List[FieldDefinition]): CreateIndexDefinition = {
      _fields = m
      this
    }

    def not(status: OperationStatus): CreateIndexDefinition = {
      _status = Some(status)
      this
    }

    override def execute: Future[CreateIndexResponse] = {
      val fs = _fields
        .groupBy(_._tp)
        .map(i => (i._1, Map("properties" -> i._2.map(_.toMap).toMap)))

      val mappings = _m match {
        case None     => fs
        case Some(ms) => ms.source._2
      }

      val ss = _s match {
        case None =>
          Map(
            "index" -> _settings.map(_.toMap).getOrElse(Map()),
            "analysis" -> Map(
              "analyzer" -> _analyzers.map(_.toMap).toMap,
              "tokenizer" -> _tokenizers.map(_.toMap).toMap
            )
          )
        case Some(se) => se.source._2
      }
      val res: Map[String, AnyRef] = Map(
        "mappings" -> mappings,
        "settings" -> ss
      )

      createIndexRequestBuilder.setSource(res.json, XContentType.JSON)

      if (_status.isDefined) {
        val indexName = createIndexRequestBuilder.request().index()
        val r: Future[IndicesExistsResponse] = client
          .admin()
          .indices()
          .prepareExists(indexName)
          .execute
        r.collect({
          case i: IndicesExistsResponse if i.isExists == false =>
            createIndexRequestBuilder.execute.actionGet()
          case _ => null
        })
      } else {
        createIndexRequestBuilder.execute
      }
    }

    override def json: String = execute.await.json
  }

  case class GetSnapshotDefinition(snapshotName: String)
      extends Definition[GetSnapshotsResponse] {
    var getSnapshotsRequestBuilder: GetSnapshotsRequestBuilder = _

    def from(repositoryName: String): GetSnapshotDefinition = {
      getSnapshotsRequestBuilder = snapshotName match {
        case "*" =>
          clusterClient
            .prepareGetSnapshots(repositoryName)
            .setSnapshots(snapshotName)
        case _ => clusterClient.prepareGetSnapshots(repositoryName)
      }
      this
    }

    override def execute: Future[GetSnapshotsResponse] = {
      getSnapshotsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class DeleteSnapshotDefinition(snapshotName: String)
      extends Definition[DeleteSnapshotResponse] {
    var deleteSnapshotRequestBuilder: DeleteSnapshotRequestBuilder = _

    def from(repositoryName: String): DeleteSnapshotDefinition = {
      deleteSnapshotRequestBuilder =
        clusterClient.prepareDeleteSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[DeleteSnapshotResponse] = {
      deleteSnapshotRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class GetMappingDefinition(
      getMappingsRequestBuilder: GetMappingsRequestBuilder)
      extends Definition[GetMappingsResponse] {
    override def execute: Future[GetMappingsResponse] = {
      getMappingsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class PutMappingRequestDefinition(
      putMappingRequestBuilder: PutMappingRequestBuilder)
      extends Definition[PutMappingResponse] {
    def mapping(m: String): PutMappingRequestDefinition = {
      putMappingRequestBuilder.setSource(m)
      this
    }

    override def execute: Future[PutMappingResponse] = {
      putMappingRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ClusterStateRequestDefinition(
      clusterStateRequestBuilder: ClusterStateRequestBuilder)
      extends Definition[ClusterStateResponse] {
    override def execute: Future[ClusterStateResponse] = {
      clusterStateRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class GetSettingsRequestDefinition(
      getSettingsRequestBuilder: GetSettingsRequestBuilder)
      extends Definition[GetSettingsResponse] {
    override def execute: Future[GetSettingsResponse] = {
      getSettingsRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class AddAliasRequestDefinition(targetAlias: String)
      extends Definition[IndicesAliasesResponse] {
    var indicesAliasesRequestBuilder: IndicesAliasesRequestBuilder = _

    def on(sourceIndex: String): AddAliasRequestDefinition = {
      indicesAliasesRequestBuilder =
        indicesClient.prepareAliases().addAlias(sourceIndex, targetAlias)
      this
    }

    override def execute: Future[IndicesAliasesResponse] = {
      indicesAliasesRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class RestoreSnapshotRequestDefinition(snapshotName: String)
      extends Definition[RestoreSnapshotResponse] {
    var restoreSnapshotRequestBuilder: RestoreSnapshotRequestBuilder = _

    def from(repositoryName: String): RestoreSnapshotRequestDefinition = {
      restoreSnapshotRequestBuilder =
        clusterClient.prepareRestoreSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[RestoreSnapshotResponse] = {
      restoreSnapshotRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class CloseIndexRequestDefinition(
      closeIndexRequestBuilder: CloseIndexRequestBuilder)
      extends Definition[CloseIndexResponse] {
    override def execute: Future[CloseIndexResponse] = {
      closeIndexRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class SearchScrollRequestDefinition(
      searchScrollRequestBuilder: SearchScrollRequestBuilder)
      extends Definition[SearchResponse] {
    override def execute: Future[SearchResponse] = {
      searchScrollRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class SearchRequestDefinition(searchRequestBuilder: SearchRequestBuilder)
      extends Definition[SearchResponse] {
    var _joinSearchRequestBuilder: Option[SearchRequestBuilder] = None
    var _tmpField: Option[String] = None
    var _dateHistogramAggregationBuilder: DateHistogramAggregationBuilder = _

    def join(indexPath: IndexPath): JoinSearchRequestDefinition = {
      size(MAX_RETRIEVE_SIZE)
      scroll("10m")
      JoinSearchRequestDefinition(ScrollSearchRequestDefinition(this),
                                  indexPath)
    }

    def by(field: String): SearchRequestDefinition = {
      _tmpField = Some(field)
      this
    }

    def where(attrType: AttrType): GetRequestDefinition = {
      val index: String = searchRequestBuilder.request().indices().head
      val tpe: String = searchRequestBuilder.request().types().head
      GetRequestDefinition(client.prepareGet().setIndex(index).setType(tpe))
    }

    def size(i: Int): SearchRequestDefinition = {
      searchRequestBuilder.setSize(i)
      this
    }

    def tpe(indexType: String): SearchRequestDefinition = {
      searchRequestBuilder.setTypes(indexType)
      this
    }

    def searchType(searchType: SearchType): SearchRequestDefinition = {
      searchRequestBuilder.setSearchType(searchType)
      this
    }

    def query(queryBuilders: List[QueryBuilder]): SearchRequestDefinition = {
      queryBuilders.foreach(i => searchRequestBuilder.setQuery(i))
      this
    }

    def query(queryBuilder: QueryBuilder): SearchRequestDefinition = {
      searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      searchRequestBuilder.setQuery(queryBuilder)
      this
    }

    def from(s: Int): SearchRequestDefinition = {
      searchRequestBuilder.setFrom(s)
      this
    }

    def must(terms: List[(String, AnyRef)]): SearchRequestDefinition = {
      val boolQuery: BoolQueryBuilder = QueryBuilders.boolQuery()
      terms.foreach(term => {
        val termQuery: TermQueryBuilder =
          QueryBuilders.termQuery(term._1, term._2)
        boolQuery.must(termQuery)
      })
      searchRequestBuilder.setQuery(boolQuery)
      this
    }

    def mth(m: (String, AnyRef)): SearchRequestDefinition = {
      val matchQueryBuilder: MatchQueryBuilder =
        QueryBuilders.matchQuery(m._1, m._2)
      searchRequestBuilder.setQuery(matchQueryBuilder)
      this
    }

    def scroll(s: String): ScrollSearchRequestDefinition = {
      searchRequestBuilder.setScroll(s)
      ScrollSearchRequestDefinition(this)
    }

    def sort(s: SortBuilder[_]): SearchRequestDefinition = {
      searchRequestBuilder.addSort(s)
      this
    }

    def avg(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(
        AggregationBuilders.avg(name).field(name))
      this
    }

    def hist(name: String): SearchRequestDefinition = {
      _dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram(name)
      this
    }

    def interval(_internalval: String): SearchRequestDefinition = {
      val interval: DateHistogramInterval = _internalval.toLowerCase() match {
        case "week"    => DateHistogramInterval.WEEK
        case "hour"    => DateHistogramInterval.HOUR
        case "second"  => DateHistogramInterval.SECOND
        case "month"   => DateHistogramInterval.MONTH
        case "year"    => DateHistogramInterval.YEAR
        case "day"     => DateHistogramInterval.DAY
        case "quarter" => DateHistogramInterval.QUARTER
        case "minute"  => DateHistogramInterval.MINUTE
      }
      _dateHistogramAggregationBuilder.dateHistogramInterval(interval)
      this
    }

    def field(_name: String): SearchRequestDefinition = {
      _dateHistogramAggregationBuilder.field(_name)
      this
    }

    def term(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(
        AggregationBuilders.terms(name).field(name).size(Integer.MAX_VALUE))
      this
    }

    override def execute: Future[SearchResponse] = {
      if (_dateHistogramAggregationBuilder != null) {
        searchRequestBuilder.addAggregation(_dateHistogramAggregationBuilder)
      }
      searchRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ScrollSearchRequestDefinition(
      searchRequestDefinition: SearchRequestDefinition)
      extends Definition[Stream[SearchHit]] {
    private def fetch(previous: String) = {
      val searchScrollRequestBuilder: SearchScrollRequestBuilder =
        client.prepareSearchScroll(previous).setScroll("10m")
      val searchRequestDefinition = SearchScrollRequestDefinition(
        searchScrollRequestBuilder)
      searchRequestDefinition.execute.await.getHits.getHits.toStream
    }

    private def toStream(scrollId: String): Stream[SearchHit] = {
      val fetch1: Stream[SearchHit] = fetch(scrollId)
      fetch1.length match {
        case 0 => Stream.empty
        case i => fetch1 #::: toStream(scrollId)
      }
    }

    private def head(searchResponse: SearchResponse): Stream[SearchHit] = {
      val scrollId = searchResponse.getScrollId
      searchResponse.getHits.getHits.toStream #::: toStream(scrollId)
    }

    def join(indexPath: IndexPath): JoinSearchRequestDefinition = {
      JoinSearchRequestDefinition(this, indexPath)
    }

    override def execute: Future[Stream[SearchHit]] = {
      searchRequestDefinition.execute.map(h => head(h))
    }

    override def json: String = execute.await.json
  }

  case class JoinSearchRequestDefinition(
      scrollSearchRequestDefinition: ScrollSearchRequestDefinition,
      indexPath: IndexPath)
      extends Definition[Stream[Map[String, AnyRef]]] {
    var _field: String = _

    def by(field: String): JoinSearchRequestDefinition = {
      _field = field
      this
    }

    override def execute: Future[Stream[Map[String, AnyRef]]] = {
      val result: Future[Stream[SearchHit]] =
        scrollSearchRequestDefinition.execute
      result.map(s => {
        s.map(i => {
          val fieldValue = i.getSourceAsMap.get(_field).asInstanceOf[String]
          val searchRequestBuilder =
            client
              .prepareSearch(indexPath.indexName)
              .setTypes(indexPath.indexType)
          val res: Stream[Map[String, AnyRef]] =
            SearchRequestDefinition(searchRequestBuilder)
              .must(List((_field, fieldValue)))
              .scroll("10m")
              .execute
              .await
              .map(t => {
                val fields
                  : mutable.Map[String, AnyRef] = t.getSourceAsMap.asScala + ("id" -> t.getId)
                fields.toMap
              })
          val doc
            : mutable.Map[String, AnyRef] = i.getSourceAsMap.asScala + ("id" -> i.getId) + (s"${indexPath.indexType}" -> res)
          doc.toMap
        })
      })
    }

    override def json: String = execute.await.json
  }

  case class PendingClusterTasksDefinition(
      pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder)
      extends Definition[PendingClusterTasksResponse] {
    override def execute: Future[PendingClusterTasksResponse] = {
      pendingClusterTasksRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class OpenIndexRequestDefinition(
      openIndexRequestBuilder: OpenIndexRequestBuilder)
      extends Definition[OpenIndexResponse] {
    override def execute: Future[OpenIndexResponse] = {
      openIndexRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class AnalyzeRequestDefinition(
      analyzeRequestBuilder: AnalyzeRequestBuilder)
      extends Definition[AnalyzeResponse] {
    def in(indexName: String): AnalyzeRequestDefinition = {
      analyzeRequestBuilder.setIndex(indexName)
      this
    }

    def analyzer(analyzer: String): AnalyzeRequestDefinition = {
      analyzeRequestBuilder.setAnalyzer(analyzer)
      this
    }

    def tokenizer(tokenizer: String): AnalyzeRequestDefinition = {
      analyzeRequestBuilder.setTokenizer(tokenizer)
      this
    }

    override def execute: Future[AnalyzeResponse] = {
      analyzeRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class DeleteIndexRequestDefinition(
      deleteIndexRequestBuilder: DeleteIndexRequestBuilder)
      extends Definition[DeleteIndexResponse] {
    override def execute: Future[DeleteIndexResponse] = {
      deleteIndexRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class DeleteRequestDefinition(deleteRequestBuilder: DeleteRequestBuilder)
      extends Definition[DeleteResponse] {
    def id(documentId: String): DeleteRequestDefinition = {
      deleteRequestBuilder.setId(documentId)
      this
    }

    override def execute: Future[DeleteResponse] = {
      deleteRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class UpdateRequestDefinition(documentId: String)
      extends Definition[UpdateResponse] {
    var updateRequestBuilder: UpdateRequestBuilder = _

    def in(indexPath: IndexPath): UpdateRequestDefinition = {
      updateRequestBuilder = client.prepareUpdate(indexPath.indexName,
                                                  indexPath.indexType,
                                                  documentId)
      this
    }

    def doc(fields: Seq[(String, Any)]): UpdateRequestDefinition = {
      updateRequestBuilder.setDoc(toJavaMap(fields.toMap))
      this
    }

    def doc(fields: Map[String, Any]): UpdateRequestDefinition = {
      updateRequestBuilder.setDoc(toJavaMap(fields))
      this
    }

    def docAsUpsert(fields: Map[String, Any]): UpdateRequestDefinition = {
      updateRequestBuilder.setDocAsUpsert(true)
      updateRequestBuilder.setDoc(toJavaMap(fields))
      this
    }

    def docAsUpsert(fields: Seq[(String, Any)]): UpdateRequestDefinition = {
      updateRequestBuilder.setDocAsUpsert(true)
      updateRequestBuilder.setDoc(toJavaMap(fields.toMap))
      this
    }

    override def execute: Future[UpdateResponse] = {
      updateRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class ShutDownRequestDefinition() extends Definition[String] {
    override def execute: Future[String] = {
      client.close()
      Future {
        "shutdown"
      }
    }

    override def json: String = "shutdown"
  }

  case class BulkRequestDefinition(bulkRequestBuilder: BulkRequestBuilder)
      extends Definition[BulkResponse] {
    override def execute: Future[BulkResponse] = bulkRequestBuilder.execute()

    override def json: String = execute.await.json
  }

  case class IndexRequestDefinition(indexRequestBuilder: IndexRequestBuilder)
      extends Definition[IndexResponse] {
    def doc(fields: Map[String, Any]): IndexRequestDefinition = {
      indexRequestBuilder.setSource(toJavaMap(fields))
      this
    }

    def doc[T <: AnyRef](d: T): IndexRequestDefinition = {
      val source = toJson(d)
      indexRequestBuilder.setSource(source)
      this
    }

    def doc(fields: List[Map[String, Any]]): BulkRequestDefinition = {
      val index = indexRequestBuilder.request().index()
      val tpe = indexRequestBuilder.request().`type`()
      val bulk = client.prepareBulk()
      fields
        .map(f => {
          val r = f.filter(!_._1.equals("id"))
          val source = client.prepareIndex(index, tpe).setSource(toJavaMap(r))
          f.get("id")
            .map(i => {
              source.setId(i.toString)
            })
          source
        })
        .foreach(i => bulk.add(i))
      BulkRequestDefinition(bulk)
    }

    def id(documentId: String): IndexRequestDefinition = {
      indexRequestBuilder.setId(documentId)
      this
    }

    def fields(fs: Seq[(String, Any)]): IndexRequestDefinition = {
      indexRequestBuilder.setSource(toJavaMap(fs.toMap))
      this
    }

    override def execute: Future[IndexResponse] = {
      indexRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class GetRequestDefinition(getRequestBuilder: GetRequestBuilder)
      extends Definition[GetResponse] {

    def equal(_id: String): GetRequestDefinition = {
      getRequestBuilder.setId(_id)
      this
    }

    override def execute: Future[GetResponse] = {
      getRequestBuilder.execute
    }

    override def json: String = execute.await.json
  }

  case class RefreshRequestDefinition(indices: String)
      extends Definition[RefreshResponse] {
    override def execute: Future[RefreshResponse] = {
      val sleep: Long = 1000
      Thread.sleep(sleep)
      client
        .admin()
        .cluster()
        .prepareHealth()
        .setWaitForNoRelocatingShards(true)
        .execute
        .actionGet()
      client
        .admin()
        .indices()
        .prepareFlush()
        .execute
        .flatMap(i =>
          indices match {
            case "*" => client.admin().indices().prepareRefresh().execute
            case _   => client.admin().indices().prepareRefresh(indices).execute
        })
    }

    override def json: String = execute.await.json
  }

  case class IndexSettingsDefinition() {
    var number_of_shards = Some(1)
    var number_of_replicas = Some(0)

    def number_of_replicas(n: Int): IndexSettingsDefinition = {
      number_of_replicas = Some(n)
      this
    }

    def number_of_shards(n: Int): IndexSettingsDefinition = {
      number_of_shards = Some(n)
      this
    }

    def toMap: Map[String, Serializable] = {
      Map("number_of_shards" -> number_of_shards.get,
          "number_of_replicas" -> number_of_replicas.get)
    }
  }

  case class NgramTokenizerDefinition(tokenizer: String) {
    var _tpe = ""
    var _tokenChars = List[String]()
    var _min_gram = 2
    var _max_gram = 2

    def tpe(tpe: String): NgramTokenizerDefinition = {
      _tpe = tpe
      this
    }

    def min_gram(min_gram: Int): NgramTokenizerDefinition = {
      _min_gram = min_gram
      this
    }

    def max_gram(max_gram: Int): NgramTokenizerDefinition = {
      _max_gram = max_gram
      this
    }

    def token_chars(token_chars: List[String]): NgramTokenizerDefinition = {
      _tokenChars = token_chars
      this
    }

    def toMap: (String, Map[String, Serializable]) = {
      tokenizer -> Map("type" -> _tpe,
                       "token_chars" -> _tokenChars,
                       "min_gram" -> _min_gram,
                       "max_gram" -> _max_gram)
    }
  }

  case class AnalyzerDefinition(analyzer: String) {
    var _tpe = ""
    var _filter = List[String]()
    var _tokenizer = ""

    def tpe(tpe: String): AnalyzerDefinition = {
      _tpe = tpe
      this
    }

    def filter(_filters: List[String]): AnalyzerDefinition = {
      _filter = _filters
      this
    }

    def tokenizer(tokenizer: String): AnalyzerDefinition = {
      _tokenizer = tokenizer
      this
    }

    def toMap: (String, Map[String, Serializable]) = {
      analyzer -> Map("type" -> _tpe,
                      "tokenizer" -> _tokenizer,
                      "filter" -> _filter)
    }
  }

  case class FieldDefinition(field: String) {
    var _tpe = ""
    var _term_vector = ""
    var _store: Boolean = false
    var _analyzer: String = ""
    var _tp: String = ""

    def tpe(tpe: String): FieldDefinition = {
      _tpe = tpe
      this
    }

    def term_vector(term_vector: String): FieldDefinition = {
      _term_vector = term_vector
      this
    }

    def store(isStore: Boolean): FieldDefinition = {
      _store = isStore
      this
    }

    def analyzer(analyzer: String): FieldDefinition = {
      _analyzer = analyzer
      this
    }

    def in(tpe: String): FieldDefinition = {
      _tp = tpe
      this
    }

    def toMap: (String, Map[String, Any]) = {
      field ->
        Map("type" -> _tpe,
            "term_vector" -> _term_vector,
            "store" -> _store,
            "analyzer" -> _analyzer)
    }
  }

  case class PropertiesDefinition()

  case class MappingDefinition(analyzer: AnalyzerDefinition,
                               properties: PropertiesDefinition)

  type IndexMappings =
    (String, Map[String, (String, Map[String, Map[String, String]])])

  import scala.reflect.runtime.universe._

  case class MappingsDefinition(tpes: TypeTag[_]*) {

    def source: IndexMappings = {
      val res =
        tpes.map(
          i => {
            val indexType = getTypeName(i.tpe)

            val fields = i.tpe.members.collect {
              case m: TermSymbol if m.isVal || m.isVar =>
                val analyzer = getAnnotationByType[Analyzer]("analyzer", m)
                val copyTo = getAnnotationByType[CopyTo]("copy_to", m)
                val index = getAnnotationByType[Index]("index", m)
                val alias = getAnnotationByType[Alias]("alias", m)

                val fieldDefinition = Map("type" -> getTypeName(
                  m.typeSignature)) ++ analyzer ++ copyTo ++ index

                if (alias.isEmpty) {
                  m.name.decodedName.toString -> fieldDefinition
                } else {
                  alias("alias") -> fieldDefinition
                }
            }

            indexType -> ("properties" -> fields.toMap)
          })
      "mappings" -> res.toMap
    }

    private def getAnnotationByType[T](name: String, m: universe.TermSymbol)(
        implicit typeTag: TypeTag[T]) = {
      m.annotations
        .find(a => a.tree.tpe <:< typeTag.tpe)
        .map(a => {
          a.tree.children.tail.map {
            case Literal(Constant(c)) => c.asInstanceOf[String]
          }.head
        })
        .map(a => name -> a)
        .toMap
    }

    private def getTypeName(i: Type): String = {
      i.typeSymbol.name.decodedName.toString.toLowerCase match {
        case "string" => "text"
        case a        => a
      }
    }
  }

  object Mappings {
    def apply[A](implicit typeTag: TypeTag[A]) = MappingsDefinition(typeTag)
    def apply[A, B](implicit typeTagA: TypeTag[A], typeTagB: TypeTag[B]) =
      MappingsDefinition(typeTagA, typeTagB)
    def apply[A, B, C](implicit typeTagA: TypeTag[A],
                       typeTagB: TypeTag[B],
                       typeTagC: TypeTag[C]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC)
    def apply[A, B, C, D](implicit typeTagA: TypeTag[A],
                          typeTagB: TypeTag[B],
                          typeTagC: TypeTag[C],
                          typeTagD: TypeTag[D]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC, typeTagD)
    def apply[A, B, C, D, E](implicit typeTagA: TypeTag[A],
                             typeTagB: TypeTag[B],
                             typeTagC: TypeTag[C],
                             typeTagD: TypeTag[D],
                             typeTagE: TypeTag[E]) =
      MappingsDefinition(typeTagA, typeTagB, typeTagC, typeTagD, typeTagE)
    def apply[A, B, C, D, E, F](implicit typeTagA: TypeTag[A],
                                typeTagB: TypeTag[B],
                                typeTagC: TypeTag[C],
                                typeTagD: TypeTag[D],
                                typeTagE: TypeTag[E],
                                typeTagF: TypeTag[F]) =
      MappingsDefinition(typeTagA,
                         typeTagB,
                         typeTagC,
                         typeTagD,
                         typeTagE,
                         typeTagF)
  }

  trait IndexSettings {

    case class Analyzer(name: String,
                        tpe: String,
                        tokenizer: String,
                        filter: String,
                        stopwordsPath: String = "") {
      def source: (String, Map[String, AnyRef]) = {
        name -> Map("type" -> tpe,
                    "tokenizer" -> tokenizer,
                    "filter" -> filter.split("\\s+,\\s+"),
                    "stopwords_path" -> stopwordsPath)
      }
    }

    case class Filter(name: String, tpe: String, keepwordsPath: String = "") {
      def source: (String, Map[String, String]) = {
        name -> Map("type" -> tpe, "keep_words_path" -> keepwordsPath)
      }
    }

    val analyzer: Analyzer
    val filter: Filter

    def source = {
      "settings" -> ("analysis" -> Map("analyzer" -> analyzer.source,
                                       "filter" -> filter.source))
    }
  }

  case class ParserErrorDefinition(parameters: Seq[Val])
      extends Definition[Map[String, AnyRef]] {

    override def execute: Future[Map[String, AnyRef]] = {
      (List("illegal_input", "caused_by") fzip parameters
        .take(2)
        .map(_.extract[String])
        .toList).toMap
        .pure[Future]
    }
    override def json: String = execute.await.json
  }

}

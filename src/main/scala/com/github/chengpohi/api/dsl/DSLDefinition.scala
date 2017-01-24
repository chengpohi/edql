package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
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
import org.elasticsearch.action.admin.indices.mapping.get.{GetMappingsRequestBuilder, GetMappingsResponse}
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.action.admin.indices.open.{OpenIndexRequestBuilder, OpenIndexResponse}
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequestBuilder, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequestBuilder, UpdateSettingsResponse}
import org.elasticsearch.action.admin.indices.stats.{IndicesStatsRequestBuilder, IndicesStatsResponse}
import org.elasticsearch.action.delete.{DeleteRequestBuilder, DeleteResponse}
import org.elasticsearch.action.get.{GetRequestBuilder, GetResponse}
import org.elasticsearch.action.index.{IndexRequestBuilder, IndexResponse}
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchResponse, SearchScrollRequestBuilder, SearchType}
import org.elasticsearch.action.update.{UpdateRequestBuilder, UpdateResponse}
import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.index.query._
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramAggregationBuilder, DateHistogramInterval}
import org.elasticsearch.search.sort.{SortBuilder, SortOrder}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * elasticdsl
  * Created by chengpohi on 6/28/16.
  */
trait DSLDefinition extends ElasticBase with DSLExecutor with DSLContext {
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticdsl"
  val DEFAULT_RETRIEVE_SIZE: Int = 500
  val MAX_ALL_NUMBER: Int = 10000
  val MAX_RETRIEVE_SIZE: Int = 500
  implicit val formats = DefaultFormats

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

  case class NodeStatsRequestDefinition(nodesStatsRequestBuilder: NodesStatsRequestBuilder) extends Definition[NodesStatsResponse] {
    def flag(f: FlagType): NodeStatsRequestDefinition = {
      nodesStatsRequestBuilder.all()
      this
    }

    override def execute: Future[NodesStatsResponse] = {
      nodesStatsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class NodeInfoRequestDefinition(nodesInfoRequestBuilder: NodesInfoRequestBuilder) extends Definition[NodesInfoResponse] {
    override def execute: Future[NodesInfoResponse] = {
      nodesInfoRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class IndicesStatsRequestDefinition(indicesStatsRequestBuilder: IndicesStatsRequestBuilder) extends Definition[IndicesStatsResponse] {
    def flag(f: FlagType): IndicesStatsRequestDefinition = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: Future[IndicesStatsResponse] = {
      indicesStatsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class ClusterStatsRequestDefinition(clusterStatsRequestBuilder: ClusterStatsRequestBuilder) extends Definition[ClusterStatsResponse] {
    override def execute: Future[ClusterStatsResponse] = {
      clusterStatsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class ClusterHealthRequestDefinition(clusterHealthRequestBuilder: ClusterHealthRequestBuilder) extends Definition[ClusterHealthResponse] {
    def timeout(time: String): ClusterHealthRequestDefinition = {
      clusterHealthRequestBuilder.setTimeout(time)
      this
    }

    def status(status: String): ClusterHealthRequestDefinition = {
      val clusterHealthStatus: ClusterHealthStatus = status match {
        case "GREEN" => ClusterHealthStatus.GREEN
        case "RED" => ClusterHealthStatus.RED
        case "YELLOW" => ClusterHealthStatus.YELLOW
        case _ => ClusterHealthStatus.GREEN
      }
      clusterHealthRequestBuilder.setWaitForStatus(clusterHealthStatus)
      this
    }

    override def execute: Future[ClusterHealthResponse] = {
      clusterHealthRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder)
    extends Definition[ClusterUpdateSettingsResponse] {
    override def execute: Future[ClusterUpdateSettingsResponse] = {
      clusterUpdateSettingsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class UpdateSettingsRequestDefinition(updateSettingsRequestBuilder: UpdateSettingsRequestBuilder) extends Definition[UpdateSettingsResponse] {
    def settings(st: String): UpdateSettingsRequestDefinition = {
      updateSettingsRequestBuilder.setSettings(st)
      this
    }

    override def execute: Future[UpdateSettingsResponse] = {
      updateSettingsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class PutRepositoryDefinition(putRepositoryRequestBuilder: PutRepositoryRequestBuilder) extends Definition[PutRepositoryResponse] {
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

    override def json: String = execute.toJson
  }

  case class CreateSnapshotDefinition(snapshotName: String) extends Definition[CreateSnapshotResponse] {
    var createSnapshotRequestBuilder: CreateSnapshotRequestBuilder = _

    def in(repositoryName: String): CreateSnapshotDefinition = {
      createSnapshotRequestBuilder = clusterClient.prepareCreateSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[CreateSnapshotResponse] = {
      createSnapshotRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class CreateIndexDefinition(createIndexRequestBuilder: CreateIndexRequestBuilder) extends Definition[CreateIndexResponse] {
    def mappings(m: String): CreateIndexDefinition = {
      createIndexRequestBuilder.setSource(m)
      this
    }

    override def execute: Future[CreateIndexResponse] = {
      createIndexRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class GetSnapshotDefinition(snapshotName: String) extends Definition[GetSnapshotsResponse] {
    var getSnapshotsRequestBuilder: GetSnapshotsRequestBuilder = _

    def from(repositoryName: String): GetSnapshotDefinition = {
      getSnapshotsRequestBuilder = snapshotName match {
        case "*" => clusterClient.prepareGetSnapshots(repositoryName).setSnapshots(snapshotName)
        case _ => clusterClient.prepareGetSnapshots(repositoryName)
      }
      this
    }

    override def execute: Future[GetSnapshotsResponse] = {
      getSnapshotsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class DeleteSnapshotDefinition(snapshotName: String) extends Definition[DeleteSnapshotResponse] {
    var deleteSnapshotRequestBuilder: DeleteSnapshotRequestBuilder = _

    def from(repositoryName: String): DeleteSnapshotDefinition = {
      deleteSnapshotRequestBuilder = clusterClient.prepareDeleteSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[DeleteSnapshotResponse] = {
      deleteSnapshotRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class GetMappingDefinition(getMappingsRequestBuilder: GetMappingsRequestBuilder) extends Definition[GetMappingsResponse] {
    override def execute: Future[GetMappingsResponse] = {
      getMappingsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class PutMappingRequestDefinition(putMappingRequestBuilder: PutMappingRequestBuilder) extends Definition[PutMappingResponse] {
    def mapping(m: String): PutMappingRequestDefinition = {
      putMappingRequestBuilder.setSource(m)
      this
    }

    override def execute: Future[PutMappingResponse] = {
      putMappingRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class ClusterStateRequestDefinition(clusterStateRequestBuilder: ClusterStateRequestBuilder) extends Definition[ClusterStateResponse] {
    override def execute: Future[ClusterStateResponse] = {
      clusterStateRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class GetSettingsRequestDefinition(getSettingsRequestBuilder: GetSettingsRequestBuilder) extends Definition[GetSettingsResponse] {
    override def execute: Future[GetSettingsResponse] = {
      getSettingsRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class AddAliasRequestDefinition(targetAlias: String) extends Definition[IndicesAliasesResponse] {
    var indicesAliasesRequestBuilder: IndicesAliasesRequestBuilder = _

    def on(sourceIndex: String): AddAliasRequestDefinition = {
      indicesAliasesRequestBuilder = indicesClient.prepareAliases().addAlias(sourceIndex, targetAlias)
      this
    }

    override def execute: Future[IndicesAliasesResponse] = {
      indicesAliasesRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class RestoreSnapshotRequestDefinition(snapshotName: String) extends Definition[RestoreSnapshotResponse] {
    var restoreSnapshotRequestBuilder: RestoreSnapshotRequestBuilder = _

    def from(repositoryName: String): RestoreSnapshotRequestDefinition = {
      restoreSnapshotRequestBuilder = clusterClient.prepareRestoreSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[RestoreSnapshotResponse] = {
      restoreSnapshotRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class CloseIndexRequestDefinition(closeIndexRequestBuilder: CloseIndexRequestBuilder) extends Definition[CloseIndexResponse] {
    override def execute: Future[CloseIndexResponse] = {
      closeIndexRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class SearchScrollRequestDefinition(searchScrollRequestBuilder: SearchScrollRequestBuilder) extends Definition[SearchResponse] {
    override def execute: Future[SearchResponse] = {
      searchScrollRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class SearchRequestDefinition(searchRequestBuilder: SearchRequestBuilder) extends Definition[SearchResponse] {
    var _joinSearchRequestBuilder: Option[SearchRequestBuilder] = None
    var _tmpField: Option[String] = None
    var _dateHistogramAggregationBuilder: DateHistogramAggregationBuilder = _

    def join(indexPath: IndexPath): SearchRequestDefinition = {
      _joinSearchRequestBuilder = Some(client.prepareSearch(indexPath.indexName).setTypes(indexPath.indexType))
      this
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
      searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
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
        val termQuery: TermQueryBuilder = QueryBuilders.termQuery(term._1, term._2)
        boolQuery.must(termQuery)
      })
      searchRequestBuilder.setQuery(boolQuery)
      this
    }

    def mth(m: (String, AnyRef)): SearchRequestDefinition = {
      val matchQueryBuilder: MatchQueryBuilder = QueryBuilders.matchQuery(m._1, m._2)
      searchRequestBuilder.setQuery(matchQueryBuilder)
      this
    }

    def scroll(s: String): ScrollSearchRequestDefinition = {
      searchRequestBuilder.setScroll(s)
      ScrollSearchRequestDefinition(this)
    }

    def order(s: SortBuilder[_]): SearchRequestDefinition = {
      searchRequestBuilder.addSort(s)
      this
    }

    def avg(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(AggregationBuilders.avg(name).field(name))
      this
    }

    def hist(name: String): SearchRequestDefinition = {
      _dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram(name)
      this
    }

    def interval(_internalval: String): SearchRequestDefinition = {
      val interval: DateHistogramInterval = _internalval.toLowerCase() match {
        case "week" => DateHistogramInterval.WEEK
        case "hour" => DateHistogramInterval.HOUR
        case "second" => DateHistogramInterval.SECOND
        case "month" => DateHistogramInterval.MONTH
        case "year" => DateHistogramInterval.YEAR
        case "day" => DateHistogramInterval.DAY
        case "quarter" => DateHistogramInterval.QUARTER
        case "minute" => DateHistogramInterval.MINUTE
      }
      _dateHistogramAggregationBuilder.dateHistogramInterval(interval)
      this
    }

    def field(_name: String): SearchRequestDefinition = {
      _dateHistogramAggregationBuilder.field(_name)
      this
    }

    def term(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(AggregationBuilders.terms(name).field(name).size(Integer.MAX_VALUE))
      this
    }

    override def execute: Future[SearchResponse] = {
      if (_dateHistogramAggregationBuilder != null) {
        searchRequestBuilder.addAggregation(_dateHistogramAggregationBuilder)
      }
      searchRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }


  case class ScrollSearchRequestDefinition(searchRequestDefinition: SearchRequestDefinition) extends Definition[Stream[SearchHit]] {
    private def fetch(previous: String) = {
      val searchScrollRequestBuilder: SearchScrollRequestBuilder = client.prepareSearchScroll(previous).setScroll("10m")
      val searchRequestDefinition = SearchScrollRequestDefinition(searchScrollRequestBuilder)
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

    override def json: String = execute.toJson
  }

  case class JoinSearchRequestDefinition(scrollSearchRequestDefinition: ScrollSearchRequestDefinition, indexPath: IndexPath)
    extends Definition[Stream[Map[String, AnyRef]]] {
    var _field: String = _

    def by(field: String): JoinSearchRequestDefinition = {
      _field = field
      this
    }

    override def execute: Future[Stream[Map[String, AnyRef]]] = {
      val result: Future[Stream[SearchHit]] = scrollSearchRequestDefinition.execute
      result.map(s => {
        s.map(i => {
          val fieldValue = i.getSource.get(_field).asInstanceOf[String]
          val searchRequestBuilder =
            client.prepareSearch(indexPath.indexName).setTypes(indexPath.indexType)
          val res: Stream[Map[String, AnyRef]] = SearchRequestDefinition(searchRequestBuilder).must(List((_field, fieldValue))).scroll("10m")
            .execute.await.map(t => {
            val fields: mutable.Map[String, AnyRef] = t.sourceAsMap.asScala + ("id" -> t.getId)
            fields.toMap
          })
          val doc: mutable.Map[String, AnyRef] = i.getSource.asScala + ("id" -> i.getId) + (s"${indexPath.indexType}" -> res)
          doc.toMap
        })
      })
    }

    override def json: String = execute.toJson
  }

  case class PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder)
    extends Definition[PendingClusterTasksResponse] {
    override def execute: Future[PendingClusterTasksResponse] = {
      pendingClusterTasksRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class OpenIndexRequestDefinition(openIndexRequestBuilder: OpenIndexRequestBuilder) extends Definition[OpenIndexResponse] {
    override def execute: Future[OpenIndexResponse] = {
      openIndexRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class AnalyzeRequestDefinition(analyzeRequestBuilder: AnalyzeRequestBuilder) extends Definition[AnalyzeResponse] {
    def in(indexName: String): AnalyzeRequestDefinition = {
      analyzeRequestBuilder.setIndex(indexName)
      this
    }

    def analyzer(analyzer: String): AnalyzeRequestDefinition = {
      analyzeRequestBuilder.setAnalyzer(analyzer)
      this
    }

    override def execute: Future[AnalyzeResponse] = {
      analyzeRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class DeleteIndexRequestDefinition(deleteIndexRequestBuilder: DeleteIndexRequestBuilder) extends Definition[DeleteIndexResponse] {
    override def execute: Future[DeleteIndexResponse] = {
      deleteIndexRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }


  case class DeleteRequestDefinition(deleteRequestBuilder: DeleteRequestBuilder) extends Definition[DeleteResponse] {
    def id(documentId: String): DeleteRequestDefinition = {
      deleteRequestBuilder.setId(documentId)
      this
    }

    override def execute: Future[DeleteResponse] = {
      deleteRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class UpdateRequestDefinition(documentId: String) extends Definition[UpdateResponse] {
    var updateRequestBuilder: UpdateRequestBuilder = _

    def in(indexPath: IndexPath): UpdateRequestDefinition = {
      updateRequestBuilder = client.prepareUpdate(indexPath.indexName, indexPath.indexType, documentId)
      this
    }

    def doc(fields: Seq[(String, String)]): UpdateRequestDefinition = {
      updateRequestBuilder.setDoc(fields.toMap.asJava)
      this
    }

    def doc(fields: String): UpdateRequestDefinition = {
      updateRequestBuilder.setDoc(fields)
      this
    }

    def docAsUpsert(fields: Seq[(String, String)]): UpdateRequestDefinition = {
      updateRequestBuilder.setDocAsUpsert(true)
      updateRequestBuilder.setDoc(fields.toMap.asJava)
      this
    }

    override def execute: Future[UpdateResponse] = {
      updateRequestBuilder.execute
    }

    override def json: String = execute.toJson
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

  case class IndexRequestDefinition(indexRequestBuilder: IndexRequestBuilder) extends Definition[IndexResponse] {
    def doc(fields: Map[String, Any]): IndexRequestDefinition = {
      val json = write(fields)
      doc(json)
      this
    }

    def doc(fields: String): IndexRequestDefinition = {
      indexRequestBuilder.setSource(fields)
      this
    }

    def id(documentId: String): IndexRequestDefinition = {
      indexRequestBuilder.setId(documentId)
      this
    }

    def fields(fs: Seq[(String, Any)]): IndexRequestDefinition = {
      indexRequestBuilder.setSource(fs.toMap.asJava)
      this
    }

    override def execute: Future[IndexResponse] = {
      indexRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class GetRequestDefinition(getRequestBuilder: GetRequestBuilder) extends Definition[GetResponse] {

    def equal(_id: String): GetRequestDefinition = {
      getRequestBuilder.setId(_id)
      this
    }

    override def execute: Future[GetResponse] = {
      getRequestBuilder.execute
    }

    override def json: String = execute.toJson
  }

  case class RefreshRequestDefinition(indices: String) extends Definition[RefreshResponse] {
    override def execute: Future[RefreshResponse] = {
      val sleep: Long = 1000
      Thread.sleep(sleep)
      client.admin().cluster().prepareHealth().setWaitForNoRelocatingShards(true).execute.actionGet()
      client.admin().indices().prepareFlush().execute.flatMap(i =>
        indices match {
          case "*" => client.admin().indices().prepareRefresh().execute
          case _ => client.admin().indices().prepareRefresh(indices).execute
        }
      )

    }

    override def json: String = execute.toJson
  }

}

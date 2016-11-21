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
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramAggregationBuilder, DateHistogramInterval}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DSLDefinition extends ElasticBase with DSLExecutor with DSLContext {
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

  case class NodeStatsRequestDefinition(nodesStatsRequestBuilder: NodesStatsRequestBuilder) extends ActionRequest[NodesStatsResponse] {
    def flag(f: FlagType): NodeStatsRequestDefinition = {
      nodesStatsRequestBuilder.all()
      this
    }

    override def execute: Future[NodesStatsResponse] = {
      nodesStatsRequestBuilder.execute
    }
  }

  case class NodeInfoRequestDefinition(nodesInfoRequestBuilder: NodesInfoRequestBuilder) extends ActionRequest[NodesInfoResponse] {
    override def execute: Future[NodesInfoResponse] = {
      nodesInfoRequestBuilder.execute
    }
  }

  case class IndicesStatsRequestDefinition(indicesStatsRequestBuilder: IndicesStatsRequestBuilder) extends ActionRequest[IndicesStatsResponse] {
    def flag(f: FlagType): IndicesStatsRequestDefinition = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: Future[IndicesStatsResponse] = {
      indicesStatsRequestBuilder.execute
    }
  }

  case class ClusterStatsRequestDefinition(clusterStatsRequestBuilder: ClusterStatsRequestBuilder) extends ActionRequest[ClusterStatsResponse] {
    override def execute: Future[ClusterStatsResponse] = {
      clusterStatsRequestBuilder.execute
    }
  }

  case class ClusterHealthRequestDefinition(clusterHealthRequestBuilder: ClusterHealthRequestBuilder) extends ActionRequest[ClusterHealthResponse] {
    def timeout(time: String): ClusterHealthRequestDefinition = {
      clusterHealthRequestBuilder.setTimeout(time)
      this
    }

    def status(clusterHealth: ClusterHealthStatus): ClusterHealthRequestDefinition = {
      clusterHealthRequestBuilder.setWaitForStatus(clusterHealth)
      this
    }

    override def execute: Future[ClusterHealthResponse] = {
      clusterHealthRequestBuilder.execute
    }
  }

  case class ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder)
    extends ActionRequest[ClusterUpdateSettingsResponse] {
    override def execute: Future[ClusterUpdateSettingsResponse] = {
      clusterUpdateSettingsRequestBuilder.execute
    }
  }

  case class UpdateSettingsRequestDefinition(updateSettingsRequestBuilder: UpdateSettingsRequestBuilder) extends ActionRequest[UpdateSettingsResponse] {
    def settings(st: String): UpdateSettingsRequestDefinition = {
      updateSettingsRequestBuilder.setSettings(st)
      this
    }

    override def execute: Future[UpdateSettingsResponse] = {
      updateSettingsRequestBuilder.execute
    }
  }

  case class PutRepositoryDefinition(putRepositoryRequestBuilder: PutRepositoryRequestBuilder) extends ActionRequest[PutRepositoryResponse] {
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
  }

  case class CreateSnapshotDefinition(snapshotName: String) extends ActionRequest[CreateSnapshotResponse] {
    var createSnapshotRequestBuilder: CreateSnapshotRequestBuilder = _

    def in(repositoryName: String): CreateSnapshotDefinition = {
      createSnapshotRequestBuilder = clusterClient.prepareCreateSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[CreateSnapshotResponse] = {
      createSnapshotRequestBuilder.execute
    }
  }

  case class CreateIndexDefinition(createIndexRequestBuilder: CreateIndexRequestBuilder) extends ActionRequest[CreateIndexResponse] {
    def mappings(m: String): CreateIndexDefinition = {
      createIndexRequestBuilder.setSource(m)
      this
    }

    override def execute: Future[CreateIndexResponse] = {
      createIndexRequestBuilder.execute
    }
  }

  case class GetSnapshotDefinition(snapshotName: String) extends ActionRequest[GetSnapshotsResponse] {
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
  }

  case class DeleteSnapshotDefinition(snapshotName: String) extends ActionRequest[DeleteSnapshotResponse] {
    var deleteSnapshotRequestBuilder: DeleteSnapshotRequestBuilder = _

    def from(repositoryName: String): DeleteSnapshotDefinition = {
      deleteSnapshotRequestBuilder = clusterClient.prepareDeleteSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[DeleteSnapshotResponse] = {
      deleteSnapshotRequestBuilder.execute
    }
  }

  case class GetMappingDefinition(getMappingsRequestBuilder: GetMappingsRequestBuilder) extends ActionRequest[GetMappingsResponse] {
    override def execute: Future[GetMappingsResponse] = {
      getMappingsRequestBuilder.execute
    }
  }

  case class PutMappingRequestDefinition(putMappingRequestBuilder: PutMappingRequestBuilder) extends ActionRequest[PutMappingResponse] {
    def mapping(m: String): PutMappingRequestDefinition = {
      putMappingRequestBuilder.setSource(m)
      this
    }

    override def execute: Future[PutMappingResponse] = {
      putMappingRequestBuilder.execute
    }
  }

  case class ClusterStateRequestDefinition(clusterStateRequestBuilder: ClusterStateRequestBuilder) extends ActionRequest[ClusterStateResponse] {
    override def execute: Future[ClusterStateResponse] = {
      clusterStateRequestBuilder.execute
    }
  }

  case class GetSettingsRequestDefinition(getSettingsRequestBuilder: GetSettingsRequestBuilder) extends ActionRequest[GetSettingsResponse] {
    override def execute: Future[GetSettingsResponse] = {
      getSettingsRequestBuilder.execute
    }
  }

  case class AddAliasRequestDefinition(targetAlias: String) extends ActionRequest[IndicesAliasesResponse] {
    var indicesAliasesRequestBuilder: IndicesAliasesRequestBuilder = _

    def on(sourceIndex: String): AddAliasRequestDefinition = {
      indicesAliasesRequestBuilder = indicesClient.prepareAliases().addAlias(sourceIndex, targetAlias)
      this
    }

    override def execute: Future[IndicesAliasesResponse] = {
      indicesAliasesRequestBuilder.execute
    }
  }

  case class RestoreSnapshotRequestDefinition(snapshotName: String) extends ActionRequest[RestoreSnapshotResponse] {
    var restoreSnapshotRequestBuilder: RestoreSnapshotRequestBuilder = _

    def from(repositoryName: String): RestoreSnapshotRequestDefinition = {
      restoreSnapshotRequestBuilder = clusterClient.prepareRestoreSnapshot(repositoryName, snapshotName)
      this
    }

    override def execute: Future[RestoreSnapshotResponse] = {
      restoreSnapshotRequestBuilder.execute
    }
  }

  case class CloseIndexRequestDefinition(closeIndexRequestBuilder: CloseIndexRequestBuilder) extends ActionRequest[CloseIndexResponse] {
    override def execute: Future[CloseIndexResponse] = {
      closeIndexRequestBuilder.execute
    }
  }

  case class SearchScrollRequestDefinition(searchScrollRequestBuilder: SearchScrollRequestBuilder) extends ActionRequest[SearchResponse] {
    override def execute: Future[SearchResponse] = {
      searchScrollRequestBuilder.execute
    }
  }

  case class SearchRequestDefinition(searchRequestBuilder: SearchRequestBuilder) extends ActionRequest[SearchResponse] {
    var _joinSearchRequestBuilder: Option[SearchRequestBuilder] = None
    var _joinField: Option[String] = None
    var _dateHistogramAggregationBuilder: DateHistogramAggregationBuilder = _

    def join(indexPath: IndexPath): SearchRequestDefinition = {
      _joinSearchRequestBuilder = Some(client.prepareSearch(indexPath.indexName).setTypes(indexPath.indexType))
      this
    }

    def by(field: String): SearchRequestDefinition = {
      _joinField = Some(field)
      this
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

    def query(query: String): SearchRequestDefinition = {
      val queryString: QueryBuilder = query match {
        case "*" => {
          searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
          QueryBuilders.matchAllQuery()
        }
        case _ => QueryBuilders.queryStringQuery(query)
      }
      searchRequestBuilder.setQuery(queryString)
      this
    }

    def query(query: QueryBuilder): SearchRequestDefinition = {
      searchRequestBuilder.setQuery(query)
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
  }


  case class ScrollSearchRequestDefinition(searchRequestDefinition: SearchRequestDefinition) extends ActionRequest[Stream[SearchResponse]] {
    private def fetch(previous: String) = {
      val searchScrollRequestBuilder: SearchScrollRequestBuilder = client.prepareSearchScroll(previous)
      val searchRequestDefinition = SearchScrollRequestDefinition(searchScrollRequestBuilder)
      searchRequestDefinition.execute.await
    }

    private def toStream(current: SearchResponse): Stream[SearchResponse] = {
      Option(current.getScrollId) match {
        case None => current #:: Stream.empty
        case Some(scrollId) => current #:: toStream(fetch(scrollId))
      }
    }

    override def execute: Future[Stream[SearchResponse]] = {
      searchRequestDefinition.execute.map(head => toStream(head))
    }
  }

  case class PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder)
    extends ActionRequest[PendingClusterTasksResponse] {
    override def execute: Future[PendingClusterTasksResponse] = {
      pendingClusterTasksRequestBuilder.execute
    }
  }

  case class OpenIndexRequestDefinition(openIndexRequestBuilder: OpenIndexRequestBuilder) extends ActionRequest[OpenIndexResponse] {
    override def execute: Future[OpenIndexResponse] = {
      openIndexRequestBuilder.execute
    }
  }

  case class AnalyzeRequestDefinition(analyzeRequestBuilder: AnalyzeRequestBuilder) extends ActionRequest[AnalyzeResponse] {
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
  }

  case class DeleteIndexRequestDefinition(deleteIndexRequestBuilder: DeleteIndexRequestBuilder) extends ActionRequest[DeleteIndexResponse] {
    override def execute: Future[DeleteIndexResponse] = {
      deleteIndexRequestBuilder.execute
    }
  }


  case class DeleteRequestDefinition(deleteRequestBuilder: DeleteRequestBuilder) extends ActionRequest[DeleteResponse] {
    def id(documentId: String): DeleteRequestDefinition = {
      deleteRequestBuilder.setId(documentId)
      this
    }

    override def execute: Future[DeleteResponse] = {
      deleteRequestBuilder.execute
    }
  }

  case class UpdateRequestDefinition(documentId: String) extends ActionRequest[UpdateResponse] {
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
  }

  case class IndexRequestDefinition(indexRequestBuilder: IndexRequestBuilder) extends ActionRequest[IndexResponse] {
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
  }

  case class GetRequestDefinition(documentId: String) extends ActionRequest[GetResponse] {
    var getRequestBuilder: GetRequestBuilder = _

    def from(indexPath: IndexPath): GetRequestDefinition = {
      getRequestBuilder = client.prepareGet(indexPath.indexName, indexPath.indexType, documentId)
      this
    }

    override def execute: Future[GetResponse] = {
      getRequestBuilder.execute
    }
  }

}

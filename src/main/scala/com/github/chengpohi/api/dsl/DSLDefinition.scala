package com.github.chengpohi.api.dsl

import com.github.chengpohi.api.ElasticBase
import org.elasticsearch.action.ActionListener
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
import org.elasticsearch.index.query.{BoolQueryBuilder, MatchQueryBuilder, QueryBuilder, QueryBuilders, TermQueryBuilder}
import org.elasticsearch.search.aggregations.AggregationBuilders

import scala.collection.JavaConverters._

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DSLDefinition extends ElasticBase with DSLExecutor {
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
    override def execute: (ActionListener[NodesStatsResponse]) => Unit = nodesStatsRequestBuilder.execute
  }

  case class NodeInfoRequestDefinition(nodesInfoRequestBuilder: NodesInfoRequestBuilder) extends ActionRequest[NodesInfoResponse] {
    override def execute: (ActionListener[NodesInfoResponse]) => Unit = nodesInfoRequestBuilder.execute
  }

  case class IndicesStatsRequestDefinition(indicesStatsRequestBuilder: IndicesStatsRequestBuilder) extends ActionRequest[IndicesStatsResponse] {
    def flag(f: FlagType): IndicesStatsRequestDefinition = {
      indicesStatsRequestBuilder.all().execute()
      this
    }

    override def execute: (ActionListener[IndicesStatsResponse]) => Unit = indicesStatsRequestBuilder.execute
  }

  case class ClusterStatsRequestDefinition(clusterStatsRequestBuilder: ClusterStatsRequestBuilder) extends ActionRequest[ClusterStatsResponse] {
    override def execute: (ActionListener[ClusterStatsResponse]) => Unit = clusterStatsRequestBuilder.execute
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

    override def execute: (ActionListener[ClusterHealthResponse]) => Unit = clusterHealthRequestBuilder.execute
  }

  case class ClusterSettingsRequestDefinition(clusterUpdateSettingsRequestBuilder: ClusterUpdateSettingsRequestBuilder)
    extends ActionRequest[ClusterUpdateSettingsResponse] {
    override def execute: (ActionListener[ClusterUpdateSettingsResponse]) => Unit = clusterUpdateSettingsRequestBuilder.execute
  }

  case class UpdateSettingsRequestDefinition(updateSettingsRequestBuilder: UpdateSettingsRequestBuilder) extends ActionRequest[UpdateSettingsResponse] {
    def settings(st: String): UpdateSettingsRequestDefinition = {
      updateSettingsRequestBuilder.setSettings(st)
      this
    }
    override def execute: (ActionListener[UpdateSettingsResponse]) => Unit = updateSettingsRequestBuilder.execute
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

    override def execute: (ActionListener[PutRepositoryResponse]) => Unit = putRepositoryRequestBuilder.execute
  }

  case class CreateSnapshotDefinition(snapshotName: String) extends ActionRequest[CreateSnapshotResponse] {
    var createSnapshotRequestBuilder: CreateSnapshotRequestBuilder = _
    def in(repositoryName: String): CreateSnapshotDefinition = {
      createSnapshotRequestBuilder = clusterClient.prepareCreateSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[CreateSnapshotResponse]) => Unit = createSnapshotRequestBuilder.execute
  }

  case class CreateIndexDefinition(createIndexRequestBuilder: CreateIndexRequestBuilder) extends ActionRequest[CreateIndexResponse] {
    def mappings(m: String): CreateIndexDefinition = {
      createIndexRequestBuilder.setSource(m)
      this
    }
    override def execute: (ActionListener[CreateIndexResponse]) => Unit = createIndexRequestBuilder.execute
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

    override def execute: (ActionListener[GetSnapshotsResponse]) => Unit = getSnapshotsRequestBuilder.execute
  }

  case class DeleteSnapshotDefinition(snapshotName: String) extends ActionRequest[DeleteSnapshotResponse] {
    var deleteSnapshotRequestBuilder: DeleteSnapshotRequestBuilder = _
    def from(repositoryName: String): DeleteSnapshotDefinition = {
      deleteSnapshotRequestBuilder = clusterClient.prepareDeleteSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[DeleteSnapshotResponse]) => Unit = deleteSnapshotRequestBuilder.execute
  }

  case class GetMappingDefinition(getMappingsRequestBuilder: GetMappingsRequestBuilder) extends ActionRequest[GetMappingsResponse] {
    override def execute: (ActionListener[GetMappingsResponse]) => Unit = getMappingsRequestBuilder.execute
  }

  case class PutMappingRequestDefinition(putMappingRequestBuilder: PutMappingRequestBuilder) extends ActionRequest[PutMappingResponse] {
    def mapping(m: String): PutMappingRequestDefinition = {
      putMappingRequestBuilder.setSource(m)
      this
    }
    override def execute: (ActionListener[PutMappingResponse]) => Unit = putMappingRequestBuilder.execute
  }

  case class ClusterStateRequestDefinition(clusterStateRequestBuilder: ClusterStateRequestBuilder) extends ActionRequest[ClusterStateResponse] {
    override def execute: (ActionListener[ClusterStateResponse]) => Unit = clusterStateRequestBuilder.execute
  }

  case class GetSettingsRequestDefinition(getSettingsRequestBuilder: GetSettingsRequestBuilder) extends ActionRequest[GetSettingsResponse] {
    override def execute: (ActionListener[GetSettingsResponse]) => Unit = getSettingsRequestBuilder.execute
  }

  case class AddAliasRequestDefinition(targetAlias: String) extends ActionRequest[IndicesAliasesResponse] {
    var indicesAliasesRequestBuilder: IndicesAliasesRequestBuilder = _
    def on(sourceIndex: String): AddAliasRequestDefinition = {
      indicesAliasesRequestBuilder = indicesClient.prepareAliases().addAlias(sourceIndex, targetAlias)
      this
    }
    override def execute: (ActionListener[IndicesAliasesResponse]) => Unit = indicesAliasesRequestBuilder.execute
  }

  case class RestoreSnapshotRequestDefinition(snapshotName: String) extends ActionRequest[RestoreSnapshotResponse] {
    var restoreSnapshotRequestBuilder: RestoreSnapshotRequestBuilder = _
    def from(repositoryName: String): RestoreSnapshotRequestDefinition = {
      restoreSnapshotRequestBuilder = clusterClient.prepareRestoreSnapshot(repositoryName, snapshotName)
      this
    }
    override def execute: (ActionListener[RestoreSnapshotResponse]) => Unit = restoreSnapshotRequestBuilder.execute
  }

  case class CloseIndexRequestDefinition(closeIndexRequestBuilder: CloseIndexRequestBuilder) extends ActionRequest[CloseIndexResponse] {
    override def execute: (ActionListener[CloseIndexResponse]) => Unit = closeIndexRequestBuilder.execute
  }

  case class SearchScrollRequestDefinition(searchScrollRequestBuilder: SearchScrollRequestBuilder) extends ActionRequest[SearchResponse] {
    override def execute: (ActionListener[SearchResponse]) => Unit = searchScrollRequestBuilder.execute
  }

  case class SearchRequestDefinition(searchRequestBuilder: SearchRequestBuilder) extends ActionRequest[SearchResponse] {
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

    def scroll(s: String): SearchRequestDefinition = {
      searchRequestBuilder.setScroll(s)
      this
    }

    def avg(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(AggregationBuilders.avg(name).field(name))
      this
    }

    def term(name: String): SearchRequestDefinition = {
      searchRequestBuilder.addAggregation(AggregationBuilders.terms(name).field(name).size(Integer.MAX_VALUE))
      this
    }

    override def execute: (ActionListener[SearchResponse]) => Unit = searchRequestBuilder.execute
  }

  case class PendingClusterTasksDefinition(pendingClusterTasksRequestBuilder: PendingClusterTasksRequestBuilder)
    extends ActionRequest[PendingClusterTasksResponse] {
    override def execute: (ActionListener[PendingClusterTasksResponse]) => Unit = pendingClusterTasksRequestBuilder.execute
  }

  case class OpenIndexRequestDefinition(openIndexRequestBuilder: OpenIndexRequestBuilder) extends ActionRequest[OpenIndexResponse] {
    override def execute: (ActionListener[OpenIndexResponse]) => Unit = openIndexRequestBuilder.execute
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

    override def execute: (ActionListener[AnalyzeResponse]) => Unit = analyzeRequestBuilder.execute
  }

  case class DeleteIndexRequestDefinition(deleteIndexRequestBuilder: DeleteIndexRequestBuilder) extends ActionRequest[DeleteIndexResponse] {
    override def execute: (ActionListener[DeleteIndexResponse]) => Unit = deleteIndexRequestBuilder.execute
  }

  case class IndexPath(indexName: String, indexType: String)

  object DSLHelper {
    implicit class IndexNameAndIndexType(indexName: String) {
      def /(indexType: String): IndexPath = {
        IndexPath(indexName, indexType)
      }
    }
  }
  case class DeleteRequestDefinition(deleteRequestBuilder: DeleteRequestBuilder) extends ActionRequest[DeleteResponse] {
    def id(documentId: String): DeleteRequestDefinition = {
      deleteRequestBuilder.setId(documentId)
      this
    }
    override def execute: (ActionListener[DeleteResponse]) => Unit = deleteRequestBuilder.execute
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
    def docAsUpsert(fields: Seq[(String, String)]): UpdateRequestDefinition = {
      updateRequestBuilder.setDocAsUpsert(true)
      updateRequestBuilder.setDoc(fields.toMap.asJava)
      this
    }
    override def execute: (ActionListener[UpdateResponse]) => Unit = updateRequestBuilder.execute
  }

  case class IndexRequestDefinition(indexRequestBuilder: IndexRequestBuilder) extends ActionRequest[IndexResponse] {
    def doc(fields: Map[String, Any]): IndexRequestDefinition = {
      indexRequestBuilder.setSource(fields.asJava)
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
    override def execute: (ActionListener[IndexResponse]) => Unit = indexRequestBuilder.execute
  }

  case class GetRequestDefinition(documentId: String) extends ActionRequest[GetResponse] {
    var getRequestBuilder: GetRequestBuilder = _

    def from(indexPath: IndexPath): GetRequestDefinition = {
      getRequestBuilder = client.prepareGet(indexPath.indexName, indexPath.indexType, documentId)
      this
    }

    override def execute: (ActionListener[GetResponse]) => Unit = getRequestBuilder.execute
  }

}

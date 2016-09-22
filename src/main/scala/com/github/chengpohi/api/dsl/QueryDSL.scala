package com.github.chengpohi.api.dsl

import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequestBuilder
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchScrollRequestBuilder}

/**
  * elasticshell
  * Created by chengpohi on 6/29/16.
  */
trait QueryDSL extends DSLDefinition with IndexerDSL{
  case object get {
    def repository(repositoryName: String): PutRepositoryDefinition = {
      val putRepository: PutRepositoryRequestBuilder = clusterClient.preparePutRepository(repositoryName)
      PutRepositoryDefinition(putRepository)
    }

    def snapshot(snapshotName: String): GetSnapshotDefinition = {
      GetSnapshotDefinition(snapshotName)
    }

    def mapping(indexName: String): GetMappingDefinition = {
      val mappingsRequestBuilder: GetMappingsRequestBuilder = indicesClient.prepareGetMappings(indexName)
      GetMappingDefinition(mappingsRequestBuilder)
    }

    def settings(indexName: String): GetSettingsRequestDefinition = {
      val getSettingsRequestBuilder: GetSettingsRequestBuilder = indicesClient.prepareGetSettings(indexName)
      GetSettingsRequestDefinition(getSettingsRequestBuilder)
    }

    def id(documentId: String): GetRequestDefinition = {
      GetRequestDefinition(documentId)
    }
  }

  case object search {
    def in(indexName: String): SearchRequestDefinition = {
      val searchRequestBuilder: SearchRequestBuilder = client.prepareSearch(indexName)
      SearchRequestDefinition(searchRequestBuilder)
    }

    def in(indexPath: IndexPath): SearchRequestDefinition = {
      val searchRequestBuilder: SearchRequestBuilder = indexPath.indexType match {
        case "*" => client.prepareSearch(indexPath.indexName)
        case _ => client.prepareSearch(indexPath.indexName).setTypes(indexPath.indexType)
      }
      SearchRequestDefinition(searchRequestBuilder)
    }
    def scroll(s: String): SearchScrollRequestDefinition = {
      val searchScrollRequestBuilder: SearchScrollRequestBuilder = client.prepareSearchScroll(s)
      SearchScrollRequestDefinition(searchScrollRequestBuilder)
    }
  }

  case object update {
    def id(documentId: String): UpdateRequestDefinition = {
      UpdateRequestDefinition(documentId)
    }

    def index(indexPath: IndexPath): PutMappingRequestDefinition = {
      val putMappingRequestBuilder: PutMappingRequestBuilder = indicesClient
        .preparePutMapping(indexPath.indexName)
        .setType(indexPath.indexType)
      PutMappingRequestDefinition(putMappingRequestBuilder)
    }
  }
}

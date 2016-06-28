package com.github.chengpohi.api.dsl

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder
import org.elasticsearch.action.delete.DeleteRequestBuilder

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DeleterDSL extends DSLDefinition {
  case object delete {
    def index(indexName: String) = {
      val deleteIndexRequestBuilder: DeleteIndexRequestBuilder = indicesClient.prepareDelete(indexName)
      DeleteIndexRequestDefinition(deleteIndexRequestBuilder)
    }

    def in(indexPath: IndexPath) = {
      val deleteRequestBuilder: DeleteRequestBuilder = client.client.prepareDelete().setIndex(indexPath.indexName).setType(indexPath.indexType)
      DeleteRequestDefinition(deleteRequestBuilder)
    }

    def snapshot(snapshotName: String) = {
      DeleteSnapshotDefinition(snapshotName)
    }
  }
}

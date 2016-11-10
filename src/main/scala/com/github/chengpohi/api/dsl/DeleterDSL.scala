package com.github.chengpohi.api.dsl

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder
import org.elasticsearch.action.delete.DeleteRequestBuilder

/**
  * elasticshell
  * Created by chengpohi on 6/28/16.
  */
trait DeleterDSL extends DSLContext {
  case object delete {
    def index(indexName: String): DeleteIndexRequestDefinition = {
      val deleteIndexRequestBuilder: DeleteIndexRequestBuilder = indicesClient.prepareDelete(indexName)
      DeleteIndexRequestDefinition(deleteIndexRequestBuilder)
    }

    def in(indexPath: IndexPath): DeleteRequestDefinition = {
      val deleteRequestBuilder: DeleteRequestBuilder = client.prepareDelete().setIndex(indexPath.indexName).setType(indexPath.indexType)
      DeleteRequestDefinition(deleteRequestBuilder)
    }

    def snapshot(snapshotName: String): DeleteSnapshotDefinition = {
      DeleteSnapshotDefinition(snapshotName)
    }
  }
}

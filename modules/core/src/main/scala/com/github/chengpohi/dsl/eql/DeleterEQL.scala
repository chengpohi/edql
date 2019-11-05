package com.github.chengpohi.dsl.eql

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder
import org.elasticsearch.action.delete.DeleteRequestBuilder

trait DeleterEQL extends EQLDefinition {
  case object delete {
    def index(indexName: String): DeleteIndexRequestDefinition = {
      val deleteIndexRequestBuilder: DeleteIndexRequestBuilder =
        indicesClient.prepareDelete(indexName)
      DeleteIndexRequestDefinition(deleteIndexRequestBuilder)
    }

    def in(indexPath: IndexPath): DeleteRequestDefinition = {
      val deleteRequestBuilder: DeleteRequestBuilder = client
        .prepareDelete()
        .setIndex(indexPath.indexName)
        .setType(indexPath.indexType)
      DeleteRequestDefinition(deleteRequestBuilder)
    }

    def snapshot(snapshotName: String): DeleteSnapshotDefinition = {
      DeleteSnapshotDefinition(snapshotName)
    }
  }
}

package com.github.chengpohi.plugin.dsl

import org.elasticsearch.action.Action
import org.elasticsearch.client.ElasticsearchClient
import org.elasticsearch.index.reindex.BulkByScrollResponse

class DSLAction(name: String) extends Action[DSLRequest, DSLResponse, DSLRequestBuilder](name) {
  override def newRequestBuilder(client: ElasticsearchClient): DSLRequestBuilder = {
    DSLRequestBuilder(client, this)
  }
  override def newResponse(): DSLResponse = {
    new DSLResponse
  }
}

object DSLAction {
  val name = "indices:data/write/reindex"
  val INSTANCE = DSLAction()

  def apply(): DSLAction = new DSLAction(name) {
  }
}

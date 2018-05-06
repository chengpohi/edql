package com.github.chengpohi.dsl

import org.elasticsearch.action.Action
import org.elasticsearch.client.ElasticsearchClient

class DSLAction(name: String)
    extends Action[DSLRequest, DSLResponse, DSLRequestBuilder](name) {
  override def newRequestBuilder(
      client: ElasticsearchClient): DSLRequestBuilder = {
    DSLRequestBuilder(client, this)
  }
  override def newResponse(): DSLResponse = {
    DSLResponse("unknown")
  }
}

object DSLAction {
  val NAME = "indices:data/write/dsl"
  val INSTANCE = DSLAction()

  def apply(): DSLAction = new DSLAction(NAME) {}
}

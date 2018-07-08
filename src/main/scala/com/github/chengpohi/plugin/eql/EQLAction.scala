package com.github.chengpohi.plugin.eql

import org.elasticsearch.action.Action
import org.elasticsearch.client.ElasticsearchClient

class EQLAction(name: String)
    extends Action[EQLRequest, EQLResponse, EQLRequestBuilder](name) {
  override def newRequestBuilder(
      client: ElasticsearchClient): EQLRequestBuilder = {
    EQLRequestBuilder(client, this)
  }
  override def newResponse(): EQLResponse = {
    EQLResponse("unknown")
  }
}

object EQLAction {
  val NAME = "indices:data/write/eql"
  val INSTANCE = EQLAction()

  def apply(): EQLAction = new EQLAction(NAME) {}
}

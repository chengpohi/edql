package com.github.chengpohi.plugin.dsl

import org.elasticsearch.action.{Action, ActionRequestBuilder}
import org.elasticsearch.client.ElasticsearchClient

class DSLRequestBuilder(client: ElasticsearchClient,
                        action: Action[DSLRequest, DSLResponse, DSLRequestBuilder]
                       ) extends
  ActionRequestBuilder[DSLRequest, DSLResponse, DSLRequestBuilder](client,
    action,
    new DSLRequest()) {
}

object DSLRequestBuilder {
  def apply(client: ElasticsearchClient,
            action: Action[DSLRequest, DSLResponse, DSLRequestBuilder]
           ): DSLRequestBuilder =
    new DSLRequestBuilder(client, action)
}

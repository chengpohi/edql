package com.github.chengpohi.plugin.eql

import org.elasticsearch.action.{Action, ActionRequestBuilder}
import org.elasticsearch.client.ElasticsearchClient

class EQLRequestBuilder(
    client: ElasticsearchClient,
    action: Action[EQLRequest, EQLResponse, EQLRequestBuilder])
    extends ActionRequestBuilder[EQLRequest, EQLResponse, EQLRequestBuilder](
      client,
      action,
      new EQLRequest()) {}

object EQLRequestBuilder {
  def apply(client: ElasticsearchClient,
            action: Action[EQLRequest, EQLResponse, EQLRequestBuilder])
    : EQLRequestBuilder =
    new EQLRequestBuilder(client, action)
}

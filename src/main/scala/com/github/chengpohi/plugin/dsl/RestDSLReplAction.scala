package com.github.chengpohi.plugin.dsl

import org.elasticsearch.client.node.NodeClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.rest.RestRequest.Method.POST
import org.elasticsearch.rest._

class RestDSLReplAction(settings: Settings, controller: RestController) extends BaseRestHandler(settings) {
  def buildRequest(request: RestRequest): DSLRequest = {
    DSLRequest()
  }
  override def getName: String = "dsl_action"
  override def prepareRequest(request: RestRequest, client: NodeClient): BaseRestHandler.RestChannelConsumer = (channel: RestChannel) => {
    val builder = channel.newBuilder()
    builder.startObject()
    builder.field("hello", "world")
    builder.endObject()
    channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder))
  }
}

object RestDSLReplAction {
  def apply(settings: Settings, controller: RestController): RestDSLReplAction = {
    val action = new RestDSLReplAction(settings, controller)
    controller.registerHandler(POST, "/_dsl", action)
    action
  }
}

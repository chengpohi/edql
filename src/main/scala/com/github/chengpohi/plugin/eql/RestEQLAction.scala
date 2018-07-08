package com.github.chengpohi.plugin.eql

import org.elasticsearch.client.node.NodeClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.rest.RestRequest.Method.{GET, POST}
import org.elasticsearch.rest._

class RestEQLAction(settings: Settings, controller: RestController)
    extends BaseRestHandler(settings) {

  override def getName: String = "eql_action"

  override def prepareRequest(
      request: RestRequest,
      client: NodeClient): BaseRestHandler.RestChannelConsumer = {
    channel: RestChannel =>
      {
        val req = new EQLRequest
        req.request = request.content().utf8ToString()
        client.executeLocally(
          new EQLAction(EQLAction.NAME),
          req,
          new EQLActionResponseListener(channel)
        )
      }
  }
}

object RestEQLAction {
  def apply(settings: Settings, controller: RestController): RestEQLAction = {
    val action = new RestEQLAction(settings, controller)
    controller.registerHandler(POST, "/_eql", action)
    controller.registerHandler(GET, "/_eql", action)
    action
  }
}

package com.github.chengpohi.dsl

import org.elasticsearch.client.node.NodeClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.rest.RestRequest.Method.{GET, POST}
import org.elasticsearch.rest._

class RestDSLAction(settings: Settings, controller: RestController) extends BaseRestHandler(settings) {

  override def getName: String = "dsl_action"

  override def prepareRequest(request: RestRequest, client: NodeClient): BaseRestHandler.RestChannelConsumer = {
    channel: RestChannel => {
      val req = new DSLRequest
      req.request = request.content().utf8ToString()
      client.executeLocally(
        new DSLAction(DSLAction.NAME),
        req,
        new DSLActionResponseListener(channel)
      )
    }
  }
}

object RestDSLAction {
  def apply(settings: Settings, controller: RestController): RestDSLAction = {
    val action = new RestDSLAction(settings, controller)
    controller.registerHandler(POST, "/_dsl", action)
    controller.registerHandler(GET, "/_dsl", action)
    action
  }
}

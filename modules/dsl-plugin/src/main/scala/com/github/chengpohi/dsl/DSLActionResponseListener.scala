package com.github.chengpohi.dsl

import java.util

import org.elasticsearch.common.xcontent.{ToXContent, XContentBuilder}
import org.elasticsearch.rest.action.RestBuilderListener
import org.elasticsearch.rest.{BytesRestResponse, RestChannel, RestResponse, RestStatus}

class DSLActionResponseListener(channel: RestChannel) extends RestBuilderListener[DSLResponse](channel) {
  private val params: util.Map[String, String] = null

  override def buildResponse(response: DSLResponse, builder: XContentBuilder): RestResponse = {
//    builder.startObject
//    response.toXContent(
//      builder,
//      new ToXContent.DelegatingMapParams(params, channel.request)
//    )
//    builder.endObject
    new BytesRestResponse(RestStatus.OK, response.result)
  }
}

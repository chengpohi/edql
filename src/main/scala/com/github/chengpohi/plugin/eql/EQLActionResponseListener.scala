package com.github.chengpohi.plugin.eql

import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.rest.action.RestBuilderListener
import org.elasticsearch.rest.{BytesRestResponse, RestChannel, RestResponse, RestStatus}

class EQLActionResponseListener(channel: RestChannel)
  extends RestBuilderListener[EQLResponse](channel) {
  override def buildResponse(response: EQLResponse,
                             builder: XContentBuilder): RestResponse = {
    //    builder.startObject
    //    response.toXContent(
    //      builder,
    //      new ToXContent.DelegatingMapParams(params, channel.request)
    //    )
    //    builder.endObject
    new BytesRestResponse(RestStatus.OK, response.result)
  }
}

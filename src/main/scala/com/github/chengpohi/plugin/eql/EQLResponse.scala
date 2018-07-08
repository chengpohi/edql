package com.github.chengpohi.plugin.eql

import org.elasticsearch.action.ActionResponse
import org.elasticsearch.common.xcontent.{
  ToXContent,
  ToXContentFragment,
  XContentBuilder
}

case class EQLResponse(result: String)
    extends ActionResponse
    with ToXContentFragment {
  override def toXContent(builder: XContentBuilder,
                          params: ToXContent.Params): XContentBuilder = {
    builder.field("result", result)
  }
}

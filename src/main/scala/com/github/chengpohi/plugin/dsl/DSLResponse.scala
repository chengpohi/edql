package com.github.chengpohi.dsl

import org.elasticsearch.action.ActionResponse
import org.elasticsearch.common.xcontent.{
  ToXContent,
  ToXContentFragment,
  XContentBuilder
}

case class DSLResponse(result: String)
    extends ActionResponse
    with ToXContentFragment {
  override def toXContent(builder: XContentBuilder,
                          params: ToXContent.Params): XContentBuilder = {
    builder.field("result", result)
  }
}

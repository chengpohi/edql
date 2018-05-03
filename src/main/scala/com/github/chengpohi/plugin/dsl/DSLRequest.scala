package com.github.chengpohi.plugin.dsl

import org.elasticsearch.action.{ActionRequest, ActionRequestValidationException, CompositeIndicesRequest}

class DSLRequest extends ActionRequest with CompositeIndicesRequest {
  var request: String = _
  override def validate(): ActionRequestValidationException = {
    null
  }
}

object DSLRequest {
  def apply: DSLRequest = new DSLRequest
}

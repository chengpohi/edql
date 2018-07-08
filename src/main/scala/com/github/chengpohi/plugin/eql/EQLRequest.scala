package com.github.chengpohi.plugin.eql

import org.elasticsearch.action.{
  ActionRequest,
  ActionRequestValidationException,
  CompositeIndicesRequest
}

class EQLRequest extends ActionRequest with CompositeIndicesRequest {
  var request: String = _
  override def validate(): ActionRequestValidationException = {
    null
  }
}

object EQLRequest {
  def apply: EQLRequest = new EQLRequest
}

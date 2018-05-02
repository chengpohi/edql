package com.github.chengpohi.plugin.dsl

import org.elasticsearch.action.CompositeIndicesRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.index.reindex.AbstractBulkIndexByScrollRequest
import org.elasticsearch.tasks.TaskId

class DSLRequest extends AbstractBulkIndexByScrollRequest[DSLRequest] with CompositeIndicesRequest {
  override def self(): DSLRequest = this
  override def forSlice(slicingTask: TaskId, slice: SearchRequest, totalSlices: Int): DSLRequest = {
    null
  }
}

object DSLRequest {
  def apply(): DSLRequest = new DSLRequest()
}

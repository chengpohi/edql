package com.github.chengpohi.dsl.eql

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder

trait AnalyzeEQL extends EQLDefinition {
  case object analyze {
    def text(text: String): AnalyzeRequestDefinition = {
      val analyzeRequestBuilder: AnalyzeRequestBuilder =
        indicesClient.prepareAnalyze(text)
      AnalyzeRequestDefinition(analyzeRequestBuilder)
    }
  }
}

package com.github.chengpohi.api

/**
 * elasticshell
 * Created by chengpohi on 3/12/16.
 */
trait ElasticAnalyzer { this: ElasticBase =>
  def analysis(analyzer: String, text: String) =
    buildFuture(client.admin.indices().prepareAnalyze(text).setAnalyzer(analyzer).execute)
}

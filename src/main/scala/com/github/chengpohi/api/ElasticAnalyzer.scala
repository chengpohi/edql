package com.github.chengpohi.api

import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticAnalyzer {
  this: ElasticBase with ElasticManagement =>
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticshell"

  def analysis(analyzer: String, text: String) =
    buildFuture(client.admin.indices().prepareAnalyze(text).setAnalyzer(analyzer).execute)

  def createAnalyzer(analyzerSetting: String) = {
    val p = Promise[UpdateSettingsResponse]()
    closeIndex(ELASTIC_SHELL_INDEX_NAME) onSuccess {
      case _ =>
        p success client.admin.indices().prepareUpdateSettings(ELASTIC_SHELL_INDEX_NAME).setSettings(analyzerSetting).execute().get()
    }
    p.future onSuccess {
      case _ => openIndex(ELASTIC_SHELL_INDEX_NAME)
    }
    p.future
  }
}

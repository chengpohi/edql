package com.github.chengpohi.api

import com.github.chengpohi.api.dsl.AnalyzeDSL
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

/**
  * elasticshell
  * Created by chengpohi on 3/12/16.
  */
trait ElasticAnalyzer extends AnalyzeDSL with ElasticManagement {
  val ELASTIC_SHELL_INDEX_NAME: String = ".elasticshell"

  def analysiser(a: String, analyzeText: String): Future[AnalyzeResponse] =
    DSL {
      analysis text analyzeText in ELASTIC_SHELL_INDEX_NAME analyzer a
    }

  def createAnalyzer(analyzerSetting: String): Future[UpdateSettingsResponse] = {
    val p = Promise[UpdateSettingsResponse]()
    closeIndex(ELASTIC_SHELL_INDEX_NAME) onSuccess {
      case _ =>
        val eventualUpdateSettingsResponse: Future[UpdateSettingsResponse] = DSL {
          indice update ELASTIC_SHELL_INDEX_NAME settings analyzerSetting
        }
        eventualUpdateSettingsResponse.onSuccess {
          case r => p success r
        }
    }
    p.future onSuccess {
      case _ => openIndex(ELASTIC_SHELL_INDEX_NAME)
    }
    p.future
  }
}

package com.github.chengpohi.api.http

import org.apache.http.util.EntityUtils
import org.elasticsearch.client.{Request, RestClient}

import scala.concurrent.{ExecutionContext, Future}

trait HttpContext {
  val restClient: RestClient

  implicit class RequestToExecutable(request: Request) {
    def execute(implicit ec: ExecutionContext): Future[String] = {
      Future {
        val entity = restClient.performRequest(request).getEntity
        EntityUtils.toString(entity)
      }
    }
  }

  def GET(path: String): Request = {
    new Request("GET", path)
  }

}

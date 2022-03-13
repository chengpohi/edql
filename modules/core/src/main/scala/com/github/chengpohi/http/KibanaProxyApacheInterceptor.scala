package com.github.chengpohi.http

import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.utils.URIBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}

class KibanaProxyApacheInterceptor extends HttpRequestInterceptor {
  override def process(request: HttpRequest, context: HttpContext): Unit = {
    request match {
      case req: HttpRequestWrapper =>
        val uri = req.getURI
        val query = uri.getQuery match {
          case null => "?pretty=false"
          case f => "?" + f + "&pretty=false"
        }
        val proxyUri = new URIBuilder(uri)
          .setPath("/api/console/proxy")
          .addParameter("path", uri.getPath.replaceAll("^/+", "") + query)
          .addParameter("method", req.getMethod)
          .build();
        req.setURI(proxyUri)
        req.addHeader("kbn-xsrf", "kibana")
      case _ =>
    }
  }
}

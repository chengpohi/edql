package com.github.chengpohi.http

import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.utils.URIBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class KibanaProxyApacheInterceptor extends HttpRequestInterceptor {
  override def process(request: HttpRequest, context: HttpContext): Unit = {
    request match {
      case req: HttpRequestWrapper =>
        val uri = req.getURI
        val query = uri.getQuery match {
          case null => "?pretty=false"
          case f => "?" + f + (if (uri.getPath.contains("_search")) "&pretty=false" else "")
        }
        val method = Option(req.getFirstHeader("KIBANA_PROXY_METHOD")).map(i => i.getValue).getOrElse("POST");
        val pathPrefix = Option(req.getFirstHeader("KIBANA_PATH_PREFIX"))
          .map(i => i.getValue)
          .filter(i => StringUtils.isNotBlank(i) && !i.equals("/"))
          .map(i => if (!i.startsWith("/")) {
            "/" + i
          } else i)
          .getOrElse("");
        val proxyUri = new URIBuilder(uri)
          .removeQuery()
          .setPath(pathPrefix + "/api/console/proxy")
          .addParameter("path", uri.getPath.replaceAll("^/+", "") + query)
          .addParameter("method", method)
          .build();
        req.setURI(proxyUri)
        req.addHeader("kbn-xsrf", "kibana")
      case _ =>
    }
  }
}

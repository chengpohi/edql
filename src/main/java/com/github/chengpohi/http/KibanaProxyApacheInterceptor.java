package com.github.chengpohi.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class KibanaProxyApacheInterceptor implements HttpRequestInterceptor {

    private static final Pattern LEADING_SLASHES_PATTERN = Pattern.compile("^/+");

    @Override
    public void process(HttpRequest request, HttpContext context) {
        if (request instanceof HttpRequestWrapper) {
            HttpRequestWrapper wrapper = (HttpRequestWrapper)request;
            URI uri = wrapper.getURI();

            String query = uri.getQuery();
            String queryString;
            if (query != null) {
                queryString = "?" + query + (uri.getPath().contains("_search") ? "&pretty=false" : "");
            } else {
                queryString = "?pretty=false";
            }

            String method = getHeaderValue(wrapper, "KIBANA_PROXY_METHOD");
            if (method == null) {
                method = "POST";
            }

            String pathPrefix = getHeaderValue(wrapper, "KIBANA_PATH_PREFIX");
            String finalPathPrefix = "";
            if (StringUtils.isNotBlank(pathPrefix) && !"/".equals(pathPrefix)) {
                finalPathPrefix = pathPrefix.startsWith("/") ? pathPrefix : "/" + pathPrefix;
            }

            URIBuilder proxyUriBuilder = new URIBuilder(uri);
            proxyUriBuilder.removeQuery();
            proxyUriBuilder.setPath(finalPathPrefix + "/api/console/proxy");
            proxyUriBuilder.addParameter("path", LEADING_SLASHES_PATTERN.matcher(uri.getPath()).replaceAll("") + queryString);
            proxyUriBuilder.addParameter("method", method);

            try {
                wrapper.setURI(proxyUriBuilder.build());
                wrapper.addHeader("kbn-xsrf", "kibana");
                wrapper.addHeader("osd-xsrf", "true");
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to build proxy URI", e);
            }
        }
    }

    private String getHeaderValue(HttpRequestWrapper request, String headerName) {
        Header header = request.getFirstHeader(headerName);
        return header != null ? header.getValue() : null;
    }
} 
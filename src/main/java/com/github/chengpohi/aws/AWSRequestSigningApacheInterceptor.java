package com.github.chengpohi.aws;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An {@link HttpRequestInterceptor} that signs requests for any AWS service
 * running in a specific region using an AWS {@link HttpSigner} and
 * {@link AwsCredentialsProvider}.
 */
public final class AWSRequestSigningApacheInterceptor implements HttpRequestInterceptor {
    private RequestSigner signer;


    /**
     * Creates an {@code AwsRequestSigningApacheInterceptor} with the
     * ability to sign request for a specific service in a region and
     * defined credentials.
     *
     * @param service                service the client is connecting to
     * @param signer                 signer implementation.
     * @param awsCredentialsProvider source of AWS credentials for signing
     * @param region                 signing region
     */
    public AWSRequestSigningApacheInterceptor(String service,
                                              HttpSigner<AwsCredentialsIdentity> signer,
                                              AwsCredentialsProvider awsCredentialsProvider,
                                              String region) {
        this.signer = new RequestSigner(service, signer, awsCredentialsProvider, region);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void process(HttpRequest request, HttpContext context)
        throws HttpException, IOException {
        URI requestUri = RequestSigner.buildUri(context, request.getRequestLine().getUri());

        // copy Apache HttpRequest to AWS request
        SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
            .method(SdkHttpMethod.fromValue(request.getRequestLine().getMethod()))
            .uri(requestUri);

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest)request;
            if (httpEntityEnclosingRequest.getEntity() != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpEntityEnclosingRequest.getEntity().writeTo(outputStream);
                if (!httpEntityEnclosingRequest.getEntity().isRepeatable()) {
                    // copy back the entity, so it can be read again
                    BasicHttpEntity entity = new BasicHttpEntity();
                    entity.setContent(new ByteArrayInputStream(outputStream.toByteArray()));
                    // wrap into repeatable entity to support retries
                    httpEntityEnclosingRequest.setEntity(new BufferedHttpEntity(entity));
                }
                requestBuilder.contentStreamProvider(() -> new ByteArrayInputStream(outputStream.toByteArray()));
            }
        }

        Map<String, List<String>> headers = headerArrayToMap(request.getAllHeaders());
        // adds a hash of the request payload when signing
        headers.put("x-amz-content-sha256", Collections.singletonList("required"));
        requestBuilder.headers(headers);
        SignedRequest signedRequest = signer.signRequest(requestBuilder.build());

        // copy everything back
        request.setHeaders(mapToHeaderArray(signedRequest.request().headers()));
    }

    private static Map<String, List<String>> headerArrayToMap(Header[] headers) {
        Map<String, List<String>> headersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Header header : headers) {
            if (!skipHeader(header)) {
                headersMap.put(header.getName(),
                               headersMap.getOrDefault(header.getName(),
                                                       new LinkedList<>(Collections.singletonList(header.getValue()))));
            }
        }
        return headersMap;
    }

    private static boolean skipHeader(Header header) {
        return (HTTP.CONTENT_LEN.equalsIgnoreCase(header.getName())
                && "0".equals(header.getValue())) // Strip Content-Length: 0
               || HTTP.TARGET_HOST.equalsIgnoreCase(header.getName())
               || "Authorization".equalsIgnoreCase(header.getName())
            ; // Host comes from endpoint
    }

    private static Header[] mapToHeaderArray(Map<String, List<String>> mapHeaders) {
        Header[] headers = new Header[mapHeaders.size()];
        int i = 0;
        for (Map.Entry<String, List<String>> headerEntry : mapHeaders.entrySet()) {
            for (String value : headerEntry.getValue()) {
                headers[i++] = new BasicHeader(headerEntry.getKey(), value);
            }
        }
        return headers;
    }
}
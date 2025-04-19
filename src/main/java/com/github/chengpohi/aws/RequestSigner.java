package com.github.chengpohi.aws;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;

class RequestSigner {
    /**
     * A service the client is connecting to.
     */
    private final String service;
    /**
     * A signer implementation.
     */
    private final HttpSigner<AwsCredentialsIdentity> signer;
    /**
     * The source of AWS credentials for signing.
     */
    private final AwsCredentialsProvider awsCredentialsProvider;
    /**
     * The signing region.
     */
    private final String region;

    /**
     *
     * @param service
     * @param signer
     * @param awsCredentialsProvider
     * @param region
     */
    RequestSigner(String service,
                  HttpSigner<AwsCredentialsIdentity> signer,
                  AwsCredentialsProvider awsCredentialsProvider,
                  String region) {
        this.service = service;
        this.signer = signer;
        this.awsCredentialsProvider = awsCredentialsProvider;
        this.region = Objects.requireNonNull(region);
    }

    /**
     * Signs the {@code request} using
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/sig-v4-header-based-auth.html">
     * AWS Signature Version 4</a>.
     *
     * @param request to be signed
     * @return signed request
     * @see AwsV4HttpSigner#sign
     */
    SignedRequest signRequest(SdkHttpFullRequest request) {
        SignedRequest signedRequest = signer.sign(r -> r.identity(awsCredentialsProvider.resolveCredentials())
            .request(request)
            .payload(request.contentStreamProvider().orElse(null))
            .putProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, service)
            .putProperty(AwsV4HttpSigner.REGION_NAME, Region.of(region).id()));

        return signedRequest;
    }

    /**
     * Returns an {@link URI} from an HTTP context.
     *
     * @param context request context
     * @param uri request line URI
     * @return an {@link URI} from an HTTP context
     * @throws IOException if the {@code uri} syntax is invalid
     */
    static URI buildUri(HttpContext context, String uri) throws IOException {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);

            HttpHost host = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            if (host != null) {
                uriBuilder.setHost(host.getHostName());
                uriBuilder.setScheme(host.getSchemeName());
                uriBuilder.setPort(host.getPort());
            }
            return uriBuilder.build();
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid URI", ex);
        }
    }
}

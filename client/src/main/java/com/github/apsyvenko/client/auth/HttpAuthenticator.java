package com.github.apsyvenko.client.auth;

import com.github.apsyvenko.client.config.MessengerConfig;
import com.github.apsyvenko.client.web.HttpClient;
import com.github.apsyvenko.client.web.ResponseFeature;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class HttpAuthenticator {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final MessengerConfig messengerConfig;
    private final HttpClient httpClient;

    public HttpAuthenticator(MessengerConfig messengerConfig, HttpClient httpClient) {
        this.messengerConfig = messengerConfig;
        this.httpClient = httpClient;
    }

    public AuthenticationData authenticate() throws ExecutionException, InterruptedException {
        HttpRequest request = createAuthRequest();

        ResponseFeature<AuthenticationData> responseFeature = this.httpClient.execute(
                messengerConfig.authServiceUri(),
                request,
                new AuthenticationResponseProcessor()
        );
        return responseFeature.get();
    }

    private HttpRequest createAuthRequest() {
        HttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                this.messengerConfig.authServiceUri().getPath()
        );

        request.headers().add(HttpHeaderNames.HOST, this.messengerConfig.authServiceUri().getHost());
        request.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

        HttpPostRequestEncoder requestEncoder;
        try {
            requestEncoder = new HttpPostRequestEncoder(request, false);
        } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
            LOGGER.error("Could not create HttpPostRequestEncoder.", e);
            throw new RuntimeException(e);
        }

        try {
            requestEncoder.addBodyAttribute("client_id", this.messengerConfig.clientId());
            requestEncoder.addBodyAttribute("client_secret",this.messengerConfig.clientSecret());
            requestEncoder.addBodyAttribute("grant_type", this.messengerConfig.grantType());
            requestEncoder.addBodyAttribute("scope", this.messengerConfig.scope());
            requestEncoder.addBodyAttribute("username", this.messengerConfig.userName());
            requestEncoder.addBodyAttribute("password", this.messengerConfig.password());
        } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
            LOGGER.error("Could add body attribute to HttpPostRequestEncoder.", e);
            throw new RuntimeException(e);
        }

        try {
            request = requestEncoder.finalizeRequest();
        } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
            LOGGER.error("Could not finalize request.", e);
            throw new RuntimeException(e);
        }

        return request;
    }

}

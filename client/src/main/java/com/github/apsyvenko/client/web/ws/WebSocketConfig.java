package com.github.apsyvenko.client.web.ws;

import com.github.apsyvenko.client.auth.AuthenticationData;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;

public record WebSocketConfig(URI uri, AuthenticationData authenticationData) {

    public WebSocketClientProtocolConfig toExternal() {
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        if (this.authenticationData != null) {
            String authorization = new StringBuilder(this.authenticationData.tokenType())
                    .append(" ")
                    .append(this.authenticationData.accessToken()).toString();

            httpHeaders.add(HttpHeaderNames.AUTHORIZATION, authorization);
        }

        return WebSocketClientProtocolConfig
                .newBuilder()
                .webSocketUri(this.uri)
                .customHeaders(httpHeaders)
                .version(WebSocketVersion.V13)
                .build();
    }

}

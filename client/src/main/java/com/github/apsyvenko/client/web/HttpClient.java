package com.github.apsyvenko.client.web;

import com.github.apsyvenko.client.web.bootstrap.BootstrapManager;
import com.github.apsyvenko.client.auth.AuthenticationData;
import com.github.apsyvenko.client.web.http.HttpResponseFeature;
import com.github.apsyvenko.client.web.ws.BasicWebSocket;
import com.github.apsyvenko.client.web.ws.WebSocketConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpRequest;

import java.net.URI;

public class HttpClient {

    private final BootstrapManager bootstrapManager;

    public HttpClient(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public <T> ResponseFeature<T> execute(
            URI uri,
            HttpRequest request,
            ResponseProcessor<T> responseProcessor
    ) {
        Bootstrap bootstrap = bootstrapManager.createHttpBootstrap();
        try {
            Channel channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();

            ResponseFeature<T> responseFeature = new HttpResponseFeature<>(responseProcessor);
            channel.attr(ResponseFeature.RESPONSE_FEATURE_ATTRIBUTE_KEY).set(responseFeature);

            channel.writeAndFlush(request);

            return responseFeature;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public  <T> BasicWebSocket<T> connectWebSocket(
            URI uri,
            AuthenticationData authenticationData,
            WebSocketProcessor<T> webSocketProcessor
    ) {
        WebSocketConfig webSocketConfig = new WebSocketConfig(uri, authenticationData);

        Bootstrap bootstrap = bootstrapManager.createWsBootstrap(webSocketConfig, webSocketProcessor);
        try {
            ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).await();
            if (channelFuture.isSuccess()) {
                Channel channel = channelFuture.sync().channel();
                return new BasicWebSocket<>(webSocketProcessor.getMessageSender(), channel);
            } else {
                throw new RuntimeException(channelFuture.cause());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

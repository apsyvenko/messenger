package com.github.apsyvenko.client;

import com.github.apsyvenko.MessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;

public class MessengerClient {

    private final URI uri;
    private final String userName;
    private final String password;

    private EventLoopGroup workerGroup;

    public MessengerClient(URI uri, String userName, String password) {
        this.uri = uri;
        this.userName = userName;
        this.password = password;
    }

    public Channel start() throws InterruptedException {
        workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new Initializer(uri));

        ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort());

        return channelFuture.sync().channel();
    }

    public void stop() {
        workerGroup.shutdownGracefully();
    }

    public static class Initializer extends ChannelInitializer<SocketChannel> {

        private final URI uri;

        public Initializer(URI uri) {
            this.uri = uri;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();

            WebSocketClientProtocolConfig webSocketClientConfig = WebSocketClientProtocolConfig
                    .newBuilder()
                    .webSocketUri(uri)
                    .version(WebSocketVersion.V13)
                    .build();

            pipeline.addLast(
                    new HttpClientCodec(),
                    new HttpObjectAggregator(8192),
                    new WebSocketClientProtocolHandler(webSocketClientConfig),
                    new MessageHandler()
            );
        }

    }
}

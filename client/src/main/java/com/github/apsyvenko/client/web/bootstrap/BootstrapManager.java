package com.github.apsyvenko.client.web.bootstrap;

import com.github.apsyvenko.client.web.http.HttpHandler;
import com.github.apsyvenko.client.web.WebSocketProcessor;
import com.github.apsyvenko.client.web.ws.WebSocketConfig;
import com.github.apsyvenko.client.web.ws.WebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

public class BootstrapManager {

    private final EventLoopGroup workerGroup;

    public BootstrapManager() {
        this.workerGroup = this.createEventLoopGroup();
    }

    private EventLoopGroup createEventLoopGroup() {
        return new NioEventLoopGroup();
    }

    public Bootstrap createHttpBootstrap() {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_CLOSE, false)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addFirst(HttpClientCodec.class.getName(), new HttpClientCodec());
                        pipeline.addAfter(
                                HttpClientCodec.class.getName(),
                                HttpObjectAggregator.class.getName(),
                                new HttpObjectAggregator(Integer.MAX_VALUE)
                        );
                        pipeline.addLast(HttpHandler.class.getName(), new HttpHandler());
                    }

                });

        return bootstrap;
    }

    public <T> Bootstrap createWsBootstrap(WebSocketConfig webSocketConfig, WebSocketProcessor<T> webSocketProcessor) {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(this.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_CLOSE, false)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addFirst(HttpClientCodec.class.getName(), new HttpClientCodec());
                        pipeline.addAfter(
                                HttpClientCodec.class.getName(),
                                HttpObjectAggregator.class.getName(),
                                new HttpObjectAggregator(Integer.MAX_VALUE)
                        );
                        pipeline.addAfter(
                                HttpObjectAggregator.class.getName(),
                                WebSocketClientProtocolHandler.class.getName(),
                                new WebSocketClientProtocolHandler(webSocketConfig.toExternal()));
                        pipeline.addLast(WebSocketHandler.class.getName(), new WebSocketHandler<>(webSocketProcessor));
                    }

                });

        return bootstrap;
    }

    public void stop() {
        this.workerGroup.shutdownGracefully();
    }

}

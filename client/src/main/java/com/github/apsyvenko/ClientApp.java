package com.github.apsyvenko;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientApp {

    private final static URI WS_URI = URI.create("ws://127.0.0.1:8080/ws/messages");
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    public static void main( String[] args ) {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                     .channel(NioSocketChannel.class)
                     .handler(new Initializer());

            ChannelFuture channelFuture = bootstrap.connect(WS_URI.getHost(), WS_URI.getPort());
            Channel channel = channelFuture.sync().channel();

            while (true) {
                Date current = new Date();
                sendMsg(channel, "Client time is - " + DATE_FORMAT.format(current));
                Thread.sleep(2000);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void sendMsg(Channel channel, String text) {
        WebSocketFrame frame = new TextWebSocketFrame(text);
        channel.writeAndFlush(frame);
    }

    public static class Initializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();

            WebSocketClientProtocolConfig webSocketClientConfig = WebSocketClientProtocolConfig
                    .newBuilder()
                    .webSocketUri(WS_URI)
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

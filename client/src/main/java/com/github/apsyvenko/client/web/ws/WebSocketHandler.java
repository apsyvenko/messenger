package com.github.apsyvenko.client.web.ws;

import com.github.apsyvenko.client.web.WebSocketProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WebSocketHandler<T> extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private final WebSocketProcessor<T> webSocketProcessor;

    public WebSocketHandler(WebSocketProcessor<T> webSocketProcessor) {
        this.webSocketProcessor = webSocketProcessor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.webSocketProcessor.onConnect(ctx.channel(), this.webSocketProcessor.getMessageSender());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame binaryWebSocketFrame) {
        T message = this.webSocketProcessor.processMessage(binaryWebSocketFrame);
        this.webSocketProcessor.onMessage(message, ctx.channel(), this.webSocketProcessor.getMessageSender());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.webSocketProcessor.onDisconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.webSocketProcessor.onError(cause);
    }

}

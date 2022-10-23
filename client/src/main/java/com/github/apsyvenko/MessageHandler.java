package com.github.apsyvenko;

import com.github.apsyvenko.schema.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class MessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        Message message = Message.getRootAsMessage(binaryWebSocketFrame.content().nioBuffer());
        System.out.println(message.text());
    }

}

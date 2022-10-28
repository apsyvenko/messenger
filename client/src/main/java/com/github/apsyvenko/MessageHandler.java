package com.github.apsyvenko;

import com.github.apsyvenko.schema.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        Message message = Message.getRootAsMessage(binaryWebSocketFrame.content().nioBuffer());
        LOGGER.info("Message - {}", message.text());
    }

}

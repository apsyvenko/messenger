package com.github.apsyvenko.client.web.ws;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class BasicMessageSender<T> implements MessageSender<T> {

    private final Function<T, ByteBuffer> messageConverter;

    public BasicMessageSender(Function<T, ByteBuffer> messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public void sendMessage(Channel channel, T message) {
        ByteBuffer byteBuffer = messageConverter.apply(message);
        ByteBuf payload = Unpooled.wrappedBuffer(byteBuffer);
        WebSocketFrame frame = new BinaryWebSocketFrame(payload);

        channel.writeAndFlush(frame);
    }

}

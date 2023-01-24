package com.github.apsyvenko.client.web;

import com.github.apsyvenko.client.web.ws.MessageSender;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public interface WebSocketProcessor<T> {

    void onConnect(Channel channel, MessageSender<T> messageSender);
    void onMessage(T message, Channel channel, MessageSender<T> messageSender);
    void onDisconnect();
    void onError(Throwable e);
    T processMessage(BinaryWebSocketFrame binaryWebSocketFrame);
    MessageSender<T> getMessageSender();

}

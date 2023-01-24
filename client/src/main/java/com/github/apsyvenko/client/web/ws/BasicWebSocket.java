package com.github.apsyvenko.client.web.ws;

import io.netty.channel.Channel;

public class BasicWebSocket<T> implements WebSocket<T> {
    private final MessageSender<T> messageSender;
    private final Channel channel;

    public BasicWebSocket(MessageSender<T> messageSender, Channel channel) {
        this.messageSender = messageSender;
        this.channel = channel;
    }

    public void sendMessage(T message) {
        this.messageSender.sendMessage(channel, message);
    }

}

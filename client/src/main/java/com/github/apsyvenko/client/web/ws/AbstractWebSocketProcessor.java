package com.github.apsyvenko.client.web.ws;

import com.github.apsyvenko.client.web.WebSocketProcessor;

import java.nio.ByteBuffer;

public abstract class AbstractWebSocketProcessor<T> implements WebSocketProcessor<T> {

    private final MessageSender<T> basicMessageSender = new BasicMessageSender<>(this::convertMessage);

    @Override
    public MessageSender<T> getMessageSender() {
        return this.basicMessageSender;
    }

    protected abstract ByteBuffer convertMessage(T message);

}

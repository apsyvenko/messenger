package com.github.apsyvenko.client.web.ws;

public interface WebSocket<T> {

    void sendMessage(T message);

}

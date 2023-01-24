package com.github.apsyvenko.client.web.ws;

import io.netty.channel.Channel;

public interface MessageSender<T> {

    void sendMessage(Channel channel, T message);

}

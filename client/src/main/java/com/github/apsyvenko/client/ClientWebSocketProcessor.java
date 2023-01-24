package com.github.apsyvenko.client;

import com.github.apsyvenko.client.web.ws.AbstractWebSocketProcessor;
import com.github.apsyvenko.client.web.ws.MessageSender;
import com.github.apsyvenko.schema.Message;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ClientWebSocketProcessor extends AbstractWebSocketProcessor<Particle> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onConnect(Channel channel, MessageSender<Particle> messageSender) {
        LOGGER.info("Client is connected.");
    }

    @Override
    public void onMessage(Particle message, Channel channel, MessageSender<Particle> messageSender) {
        LOGGER.info(message.text());
    }

    @Override
    public void onDisconnect() {
        LOGGER.info("Client is disconnected.");
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.error("Error", e);
    }

    @Override
    public Particle processMessage(BinaryWebSocketFrame binaryWebSocketFrame) {
        Message message = Message.getRootAsMessage(binaryWebSocketFrame.content().nioBuffer());
        return new Particle(message.text());
    }

    @Override
    protected ByteBuffer convertMessage(Particle message) {
        return message.pack();
    }
}

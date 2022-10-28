package com.github.apsyvenko.server.web.socket;

import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import com.github.apsyvenko.schema.*;

import java.nio.ByteBuffer;

public class MessageHandler extends BinaryWebSocketHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        LOGGER.info("Connection is established - {}", session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        Message messageFromClient = Message.getRootAsMessage(message.getPayload());

        LOGGER.info("Got message: {} from {}", messageFromClient.text(), session.getId());

        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int processedByServerOffset = builder.createString("Processed by server - " + messageFromClient.text());
        Message.startMessage(builder);
        Message.addText(builder, processedByServerOffset);
        int messageOffset = Message.endMessage(builder);
        builder.finish(messageOffset);
        ByteBuffer payload = builder.dataBuffer();

        session.sendMessage(new BinaryMessage(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        LOGGER.info("Connection is closed - {}, reason - {}", session.getId(), status.getReason());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        LOGGER.error("Error, sid - {}, details - {}", session.getId(), exception.getLocalizedMessage());
    }
}

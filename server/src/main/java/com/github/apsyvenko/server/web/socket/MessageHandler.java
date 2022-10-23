package com.github.apsyvenko.server.web.socket;

import com.google.flatbuffers.FlatBufferBuilder;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import com.github.apsyvenko.schema.*;

import java.nio.ByteBuffer;

public class MessageHandler extends BinaryWebSocketHandler {

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        Message messageFromClient = Message.getRootAsMessage(message.getPayload());

        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int processedByServerOffset = builder.createString("Processed by server - " + messageFromClient.text());
        Message.startMessage(builder);
        Message.addText(builder, processedByServerOffset);
        int messageOffset = Message.endMessage(builder);
        builder.finish(messageOffset);
        ByteBuffer payload = builder.dataBuffer();

        session.sendMessage(new BinaryMessage(payload));
    }

}

package com.github.apsyvenko.server.web.socket;

import com.github.apsyvenko.schema.Message;
import com.google.flatbuffers.FlatBufferBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageHandlerTest {

    private static final String URL_TEMPLATE = "ws://127.0.0.1:%d/ws/messages";

    @LocalServerPort
    private Integer port;

    private WebSocketClient webSocketClient;
    private BlockingQueue<Message> responseHolder;


    @BeforeEach
    public void setUp() {
        this.webSocketClient = new StandardWebSocketClient();
        this.responseHolder = new ArrayBlockingQueue<>(1);
    }

    @Test
    public void testEcho() throws ExecutionException, InterruptedException, IOException {
        TestWebSocketHandler handler = new TestWebSocketHandler(responseHolder);
        WebSocketSession session = webSocketClient.doHandshake(handler, String.format(URL_TEMPLATE, port)).get();

        String messageFromClientString = "Tests message";
        session.sendMessage(new BinaryMessage(buildMessageFromString(messageFromClientString)));

        await().atMost(1, SECONDS).untilAsserted(() -> {
            assertFalse(responseHolder.isEmpty());

            Message messageFromServer = responseHolder.take();
            assertEquals("Processed by server - " + messageFromClientString, messageFromServer.text());
        });
    }

    private ByteBuffer buildMessageFromString(String str) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int processedByServerOffset = builder.createString(str);
        Message.startMessage(builder);
        Message.addText(builder, processedByServerOffset);
        int messageOffset = Message.endMessage(builder);
        builder.finish(messageOffset);

        return builder.dataBuffer();
    }

    public static class TestWebSocketHandler implements WebSocketHandler {

        private final BlockingQueue<Message> responseHolder;

        public TestWebSocketHandler(BlockingQueue<Message> responseHolder) {
            this.responseHolder = responseHolder;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

            if (message instanceof BinaryMessage binaryMessage) {
                Message messageFromServer = Message.getRootAsMessage(binaryMessage.getPayload());
                responseHolder.add(messageFromServer);
            } else {
                throw new IllegalAccessException("Unsupported message type.");
            }

        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        }

        @Override
        public boolean supportsPartialMessages() {
            return false;
        }
    }

}
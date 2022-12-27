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
import java.net.URI;
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
    private static final String MOCK_TOKEN = "" +
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJldkRvZUxEQUFjY" +
            "Ud0ZzNyUlBtYnQ4SUE1b05aVUhkRThHOV9EZzlLcFFjIn0.eyJleHAiOjE3NTQyND" +
            "cwNzQsImlhdCI6MTY2Nzg0NzA3NCwianRpIjoiYTFjZmU3NGItMzExYi00NzA4LTh" +
            "mZTEtZjQwYWQ4YTVjZDFiIiwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo5OTkwL3Jl" +
            "YWxtcy9tZXNzZW5nZXIiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZWIyOWI1ZTktO" +
            "DE0Yy00NDU2LTlkMDktODNjOTU4MjNkZThkIiwidHlwIjoiQmVhcmVyIiwiYXpwIj" +
            "oibWVzc2VuZ2VyLXNlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiZWI2NDAxMjAtZmR" +
            "kYy00YzYwLTg4NWQtOTEwYzk3NGIwZDFhIiwiYWNyIjoiMSIsInJlYWxtX2FjY2Vz" +
            "cyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLW1lc3NlbmdlciIsIm9mZmxpbmVfY" +
            "WNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJVU0VSIl19LCJyZXNvdXJjZV9hY2" +
            "Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmF" +
            "nZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVu" +
            "aWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6ImViNjQwMTIwLWZkZGMtNGM2MC04ODVkL" +
            "TkxMGM5NzRiMGQxYSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoidXNlci" +
            "0xLWZpcnN0LW5hbWUgdXNlci0xLWxhc3QtbmFtZSIsInByZWZlcnJlZF91c2VybmF" +
            "tZSI6InVzZXItMSIsImdpdmVuX25hbWUiOiJ1c2VyLTEtZmlyc3QtbmFtZSIsImZh" +
            "bWlseV9uYW1lIjoidXNlci0xLWxhc3QtbmFtZSIsImVtYWlsIjoidXNlci0xQHRlc" +
            "3QubWFpbCJ9.nZTzIyx8nzI2OCH3s-96hPLzwHyyIuVMuI7Ft2XHXqA1icwu00ivU" +
            "Q6xlW2EuraL3s_wCLmMHrF2La6pqIeTdSFdduxEJr38kjH8jwnOjXpK77uZ-vnASs" +
            "6gqE4W__9hoxPPLgavvmqASk-HjUAuX4WL6-IQDWm-WBL712jFv1wVfdUhk1qckFU" +
            "h2sTZQOUyE0f49Pxqw_PFyIt9GgO_vmNE287OPSnsoIdqTfRLuHrBC4FigZJ4nIca" +
            "sY65PO5843YACA-X6-XMrIKII2oIEnzbnJV3051c4DZkawT4XMJYzUNLZ6tfuApDG" +
            "ks_UxcmNt6lofqYnoprukA0Z-GdNw";

    @LocalServerPort
    private Integer port;

    private WebSocketClient webSocketClient;
    private BlockingQueue<Message> responseHolder;


    @BeforeEach
    public void setUp() {
        this.webSocketClient = new StandardWebSocketClient();
        this.responseHolder = new ArrayBlockingQueue<>(1);
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


    @Test
    public void testEcho() throws ExecutionException, InterruptedException, IOException {
        TestWebSocketHandler handler = new TestWebSocketHandler(responseHolder);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + MOCK_TOKEN);
        WebSocketSession session = webSocketClient.doHandshake(handler, headers, URI.create(String.format(URL_TEMPLATE, port))).get();

        String messageFromClientString = "Tests message";
        session.sendMessage(new BinaryMessage(buildMessageFromString(messageFromClientString)));

        await().atMost(1, SECONDS).untilAsserted(() -> {
            assertFalse(responseHolder.isEmpty());

            Message messageFromServer = responseHolder.take();
            assertEquals("Processed by server - " + messageFromClientString, messageFromServer.text());
        });
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
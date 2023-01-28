package com.github.apsyvenko.server.web.socket;

import com.github.apsyvenko.common.Particle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageHandler extends BinaryWebSocketHandler {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        LOGGER.info("Connection is established - {}", session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        ByteBuffer inPayload = message.getPayload();
        Particle inParticle = Particle.unpack(inPayload);

        LOGGER.info("Got message: {} from {}", inParticle.body(), session.getId());

        String outText = "Processed by server - " + inParticle.body() + " - " + DATE_FORMAT.format(new Date());
        Particle outParticle = new Particle(outText);
        ByteBuffer outPayload = outParticle.pack();

        session.sendMessage(new BinaryMessage(outPayload));
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

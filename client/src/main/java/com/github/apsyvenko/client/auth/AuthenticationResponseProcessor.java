package com.github.apsyvenko.client.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.apsyvenko.client.web.ResponseProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationResponseProcessor implements ResponseProcessor<AuthenticationData> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AuthenticationData process(FullHttpResponse response) {
        ByteBuf content = response.content();
        AuthenticationData authenticationData;

        try {
            authenticationData = this.mapper.readValue(content.toString(CharsetUtil.UTF_8), AuthenticationData.class);
        } catch (JsonProcessingException e) {
            this.LOGGER.error("Can't parse authentication data.", e);
            throw new RuntimeException((e));
        }

        return authenticationData;
    }

}

package com.github.apsyvenko.client.config;

import java.net.URI;

public record MessengerConfig(
        URI messagesServiceUri,
        URI authServiceUri,
        String clientId,
        String clientSecret,
        String grantType,
        String scope,
        String userName,
        String password
) {

}

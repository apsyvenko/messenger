package com.github.apsyvenko;

import com.github.apsyvenko.client.ClientWebSocketProcessor;
import com.github.apsyvenko.client.auth.AuthenticationData;
import com.github.apsyvenko.client.auth.HttpAuthenticator;
import com.github.apsyvenko.client.web.HttpClient;
import com.github.apsyvenko.client.web.bootstrap.BootstrapManager;
import com.github.apsyvenko.client.Particle;
import com.github.apsyvenko.client.config.MessengerConfig;
import com.github.apsyvenko.client.web.ws.BasicWebSocket;
import com.github.apsyvenko.util.cli.CommandLineParser;
import com.github.apsyvenko.util.cli.ExecutionParameters;
import com.github.apsyvenko.util.cli.Option;
import com.github.apsyvenko.util.cli.Options;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ClientApp {

    private static final String HTTP_PROTOCOL = "http://";
    private static final String WS_PROTOCOL = "ws://";
    private static final String DEFAULT_URI = "127.0.0.1:8080";
    private static final String AUTH_PATH = "/auth-service/realms/messenger/protocol/openid-connect/token";
    private static final String MESSAGES_PATH = "/messenger-service/ws/messages";
    private static final String AUTH_URI = HTTP_PROTOCOL + DEFAULT_URI + AUTH_PATH;

    private static final String MESSAGES_URI = WS_PROTOCOL + DEFAULT_URI + MESSAGES_PATH;
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public static void main(String[] args) throws Exception {

        Option urlParameter = new Option("url", true, "Server URL");
        Option authServiceUrlParameter = new Option("authServiceUrl", true, "Auth service URL");
        Option clientIdParameter = new Option("clientId", true, "Client application ID");
        Option clientSecretParameter = new Option("clientSecret", true, "Client application secret");
        Option grantTypeParameter = new Option("grantType", true, "Client application grant type");
        Option scopeParameter = new Option("scope", true, "Client application scopeParameter");
        Option userParameter = new Option("user", true, "Username");
        Option passwordParameter = new Option("password", true, "Password");

        Options options = new Options()
                .addOption(urlParameter)
                .addOption(authServiceUrlParameter)
                .addOption(clientIdParameter)
                .addOption(clientSecretParameter)
                .addOption(grantTypeParameter)
                .addOption(scopeParameter)
                .addOption(userParameter)
                .addOption(passwordParameter);

        ExecutionParameters executionParameters = CommandLineParser.parse(args, options);

        if (executionParameters.isEmpty()) {
            return;
        }

        MessengerConfig messengerConfig = new MessengerConfig(
                URI.create(executionParameters.getParameter(urlParameter, MESSAGES_URI)),
                URI.create(executionParameters.getParameter(authServiceUrlParameter, AUTH_URI)),
                executionParameters.getParameter(clientIdParameter, ""),
                executionParameters.getParameter(clientSecretParameter, ""),
                executionParameters.getParameter(grantTypeParameter, ""),
                executionParameters.getParameter(scopeParameter, ""),
                executionParameters.getParameter(userParameter, ""),
                executionParameters.getParameter(passwordParameter, "")
        );


        BootstrapManager bootstrapManager = new BootstrapManager();
        HttpClient httpClient = new HttpClient(bootstrapManager);

        HttpAuthenticator httpAuthenticator = new HttpAuthenticator(messengerConfig, httpClient);
        AuthenticationData authenticationData = httpAuthenticator.authenticate();

        BasicWebSocket<Particle> webSocket = httpClient.connectWebSocket(
                messengerConfig.messagesServiceUri(),
                authenticationData,
                new ClientWebSocketProcessor()
        );

        while (true) {
            Particle particle = new Particle("From client.");
            webSocket.sendMessage(particle);
            Thread.sleep(2000);
        }
    }

}

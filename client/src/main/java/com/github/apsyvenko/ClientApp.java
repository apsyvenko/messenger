package com.github.apsyvenko;

import com.github.apsyvenko.client.MessengerClient;
import com.github.apsyvenko.schema.Message;
import com.github.apsyvenko.util.cli.CommandLineParser;
import com.github.apsyvenko.util.cli.ExecutionParameters;
import com.github.apsyvenko.util.cli.Option;
import com.github.apsyvenko.util.cli.Options;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientApp {

    private static final String WS_URI = "ws://127.0.0.1:8080/ws/messages";
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    public static void main( String[] args ) throws IllegalAccessException, InterruptedException {

        Option urlParameter = new Option("url", true, "Server URL");
        Option userParameter = new Option("user", true, "Username");
        Option passwordParameter = new Option("password", true, "Password");

        Options options = new Options()
                .addOption(urlParameter)
                .addOption(userParameter)
                .addOption(passwordParameter);

        ExecutionParameters executionParameters = CommandLineParser.parse(args, options);

        if (executionParameters.isEmpty()) {
            return;
        }

        URI uri = URI.create(executionParameters.getParameter(urlParameter, WS_URI));
        String userName = executionParameters.getParameter(userParameter, "");
        String password = executionParameters.getParameter(passwordParameter, "");

        MessengerClient messengerClient = new MessengerClient(uri, userName, password);
        Channel channel = messengerClient.start();

        while (true) {
            Date current = new Date();
            sendMsg(channel, "Client time is - " + DATE_FORMAT.format(current));
            Thread.sleep(2000);
        }
    }

    public static void sendMsg(Channel channel, String text) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int processedByServerOffset = builder.createString(text);
        Message.startMessage(builder);
        Message.addText(builder, processedByServerOffset);
        int messageOffset = Message.endMessage(builder);
        builder.finish(messageOffset);
        ByteBuffer payload = builder.dataBuffer();
        WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(payload));
        channel.writeAndFlush(frame);
    }

}

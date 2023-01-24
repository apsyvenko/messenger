package com.github.apsyvenko.client;

import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;

public record Particle(String text) {

    public ByteBuffer pack() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int processedByServerOffset = builder.createString(text);

        com.github.apsyvenko.schema.Message.startMessage(builder);
        com.github.apsyvenko.schema.Message.addText(builder, processedByServerOffset);

        int messageOffset = com.github.apsyvenko.schema.Message.endMessage(builder);

        builder.finish(messageOffset);
        return builder.dataBuffer();
    }

}

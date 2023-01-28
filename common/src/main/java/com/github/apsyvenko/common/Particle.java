package com.github.apsyvenko.common;

import com.github.apsyvenko.schema.RawParticle;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;

public record Particle(String body) {

    private Particle(RawParticle rawParticle) {
        this(rawParticle.body());
    }
    public static Particle unpack(ByteBuffer byteBuffer) {
        RawParticle rawParticle = RawParticle.getRootAsRawParticle(byteBuffer);
        return new Particle(rawParticle);
    }

    public ByteBuffer pack() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int bodyOffset = builder.createString(body);

        RawParticle.startRawParticle(builder);
        RawParticle.addBody(builder, bodyOffset);

        int rawParticleOffset = RawParticle.endRawParticle(builder);
        builder.finish(rawParticleOffset);

        return builder.dataBuffer();
    }

}

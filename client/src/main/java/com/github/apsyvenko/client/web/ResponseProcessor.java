package com.github.apsyvenko.client.web;

import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseProcessor<T> {

    T process(FullHttpResponse response);

}

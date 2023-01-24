package com.github.apsyvenko.client.web.http;

import com.github.apsyvenko.client.web.ResponseFeature;
import com.github.apsyvenko.client.web.ResponseProcessor;

public class HttpResponseFeature<T> extends ResponseFeature<T> {

    private final ResponseProcessor<T> responseProcessor;

    public HttpResponseFeature(ResponseProcessor<T> responseProcessor) {
        this.responseProcessor = responseProcessor;
    }

    public ResponseProcessor<T> getResponseProcessor() {
        return responseProcessor;
    }

}

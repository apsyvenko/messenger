package com.github.apsyvenko.client.web;

import io.netty.util.AttributeKey;

import java.util.concurrent.*;

public class ResponseFeature<T> implements Future<T> {

    public static final AttributeKey<Object> RESPONSE_FEATURE_ATTRIBUTE_KEY = AttributeKey.valueOf("RESPONSE_FEATURE_ATTRIBUTE_KEY");

    private final CountDownLatch latch = new CountDownLatch(1);

    private T content;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.latch.getCount() == 0;
    }

    @Override
    public T get() throws InterruptedException {
        this.latch.await();
        return this.content;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        this.latch.await(timeout, unit);
        return this.content;
    }

    public void setContent(T content) {
        this.content = content;
        this.latch.countDown();
    }

}

package com.github.apsyvenko.client.web.http;

import com.github.apsyvenko.client.web.ResponseFeature;
import com.github.apsyvenko.client.web.http.HttpResponseFeature;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

@SuppressWarnings("unchecked")
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        Object responseFeatureAttribute = ctx.channel().attr(ResponseFeature.RESPONSE_FEATURE_ATTRIBUTE_KEY).get();

        if (responseFeatureAttribute instanceof HttpResponseFeature responseFeature) {
            Object data = responseFeature.getResponseProcessor().process(msg);
            responseFeature.setContent(data);
        }
    }

}

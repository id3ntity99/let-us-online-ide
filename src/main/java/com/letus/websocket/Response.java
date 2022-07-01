package com.letus.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

import javax.annotation.CheckForNull;

public class Response {
    @CheckForNull
    private final ByteBuf content;
    @CheckForNull
    private final String contentType;
    @CheckForNull
    private final HttpResponseStatus status;
    @CheckForNull
    private static final HttpVersion version = HttpVersion.HTTP_1_1;

    public Response(ResponseBuilder builder) {
        this.content = builder.content;
        this.contentType = builder.contentType;
        this.status = builder.status;
    }

    public HttpResponse getHttpResponse() {
        int contentLen = content.readableBytes();
        HttpResponse response = new DefaultFullHttpResponse(
                version, status, content
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLen);
        return response;
    }

    public static class ResponseBuilder {
        @CheckForNull
        private ByteBuf content;
        @CheckForNull
        private String contentType;
        @CheckForNull
        private HttpResponseStatus status;

        public ResponseBuilder withContent(ByteBuf content) {
            this.content = content;
            return this;
        }

        public ResponseBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ResponseBuilder withStatus(HttpResponseStatus status) {
            this.status = status;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}

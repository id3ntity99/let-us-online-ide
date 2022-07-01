package com.letus.websocket;

import com.letus.user.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebSocketInitHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Channel, User> chanUserHash;

    public WebSocketInitHandler(Map<Channel, User> chanUserHash) {
        super();
        this.chanUserHash = chanUserHash;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest req) {
        boolean isUpgradeReq = req.headers().get(HttpHeaderNames.CONNECTION).equalsIgnoreCase("Upgrade");
        boolean isWsUpgradeReq = req.headers().get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("WebSocket");
        if (isUpgradeReq && isWsUpgradeReq) {
            ctx.pipeline().replace(this, "websocketFrameHandler", new WebsocketFrameHandler(chanUserHash));
            handleHandshake(ctx, req);
        } else {
            logger.debug("Invalid request (Expected HTTP upgrade request)");
            ByteBuf content = Unpooled.copiedBuffer("HTTP Upgrade request expected", CharsetUtil.UTF_8);
            HttpResponse response = new Response.ResponseBuilder()
                    .withStatus(HttpResponseStatus.BAD_REQUEST)
                    .withContent(content)
                    .withContentType("application/json")
                    .build()
                    .getHttpResponse();
            ctx.writeAndFlush(response);
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        String hostHeader = req.headers().get("Host");
        String uriHeader = req.uri();
        String requestedUrl = "ws://" + hostHeader + uriHeader;
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(requestedUrl, null, true);
        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);
        handShaker.handshake(ctx.channel(), req);
    }
}

package client.nettyserver.handlers;

import client.docker.dockerclient.proxy.DockerClient;
import client.docker.dockerclient.proxy.NettyDockerClient;
import client.nettyserver.listeners.ListenAndReadListener;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketInitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private NettyDockerClient dockerClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive() called");
        Channel inboundChannel = ctx.channel();
        dockerClient = new NettyDockerClient().withInboundChannel(inboundChannel)
                .withOutChannelClass(ctx.channel().getClass())
                .withAddress("localhost", 2375)
                .withEventLoop(ctx.channel().eventLoop())
                .bootstrap();
        dockerClient.connect().sync().addListener(new ListenAndReadListener(inboundChannel));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        boolean isUpgradeReq = req.headers().get(HttpHeaderNames.CONNECTION).equalsIgnoreCase("Upgrade");
        boolean isWsUpgradeReq = req.headers().get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("WebSocket");
        if (isUpgradeReq && isWsUpgradeReq) {
            ctx.pipeline().addLast(new CreateContainerHandler(dockerClient));
            ctx.pipeline().fireUserEventTriggered(isUpgradeReq);
            logger.info("Upgrading the connection...");
            handleHandshake(ctx, req).sync().addListener(new WsHandshakeListener(ctx, dockerClient));
        } else {
            logger.debug("Invalid request (Expected HTTP upgrade request)");
            HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            ctx.writeAndFlush(res);
        }
    }

    private ChannelFuture handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        logger.debug("Handshaking...");
        String hostHeader = req.headers().get("Host");
        String uriHeader = req.uri();
        String requestedUrl = "ws://" + hostHeader + uriHeader;
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(requestedUrl, null, true);
        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);
        return handShaker.handshake(ctx.channel(), req);
    }

    private static class WsHandshakeListener implements ChannelFutureListener {
        private final ChannelHandlerContext ctx;
        private final NettyDockerClient dockerClientInstance;

        public WsHandshakeListener(ChannelHandlerContext ctx, NettyDockerClient dockerClientInstance) {
            this.ctx = ctx;
            this.dockerClientInstance = dockerClientInstance;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                ctx.pipeline().remove(WebSocketInitHandler.class);
                ctx.pipeline().addLast(new WebSocketFrameHandler(dockerClientInstance));
                ctx.pipeline().fireChannelActive();
            }
        }
    }
}
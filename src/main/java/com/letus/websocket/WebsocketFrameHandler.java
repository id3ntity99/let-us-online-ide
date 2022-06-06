package com.letus.websocket;

import com.letus.user.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Map;

public class WebsocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Map<Channel, User> chanUserHash;

    public WebsocketFrameHandler(Map<Channel, User> chanUserHash) {
        this.chanUserHash = chanUserHash;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws IOException {
        User user = chanUserHash.get(ctx.channel());
        PipedOutputStream out = user.getOutputStream();
        ByteBuf frameBuf = frame.content();
        byte[] byteFrame = new byte[frameBuf.readableBytes()];
        frameBuf.duplicate().readBytes(byteFrame);
        out.write(byteFrame);
        out.flush();
    }
}

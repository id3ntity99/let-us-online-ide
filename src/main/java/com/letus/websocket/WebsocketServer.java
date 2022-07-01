package com.letus.websocket;

import com.letus.user.User;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.HashMap;

public class WebsocketServer {
    private static final int PORT = 9000;
    private static final HashMap<String, User> idUserHash = new HashMap<>();
    private static final HashMap<Channel, User> chanUserHash = new HashMap<>();

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new AuthHandler(chanUserHash, idUserHash));
                            ch.pipeline().addLast(new UserInitHandler(chanUserHash, idUserHash));
                            ch.pipeline().addLast(new SaveUserHandler(chanUserHash));
                            ch.pipeline().addLast(new WebSocketInitHandler(chanUserHash));
                        }
                    });
            Channel channel = b.bind(PORT).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

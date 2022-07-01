package com.letus.websocket;

import com.letus.user.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

import java.util.Map;

public class SaveUserHandler extends SimpleUserEventChannelHandler<User> {
    private final Map<Channel, User> userHashMap;

    public SaveUserHandler(Map<Channel, User> userHashMap) {
        super();
        this.userHashMap = userHashMap;
    }
    @Override
    public void eventReceived(ChannelHandlerContext ctx, User user) {
        Channel userChannel = user.getChannel();
        userHashMap.put(userChannel, user);
        ctx.pipeline().remove(this);
    }
}

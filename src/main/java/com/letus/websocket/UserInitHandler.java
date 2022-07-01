package com.letus.websocket;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.Client;
import com.letus.docker.command.CreateCmd;
import com.letus.docker.command.CreateExecCmd;
import com.letus.docker.command.StartCmd;
import com.letus.docker.command.StartExecCmd;
import com.letus.user.Profile;
import com.letus.user.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;

public class UserInitHandler extends SimpleUserEventChannelHandler<Profile> {
    private final DockerClient dockerClient = Client.DOCKER_CLIENT.getDockerClient();
    private final Map<String, User> idUserHash;
    private final Map<Channel, User> userHashMap;

    public UserInitHandler(Map<Channel, User> userHashMap, Map<String, User> idUserHash) {
        super();
        this.userHashMap = userHashMap;
        this.idUserHash = idUserHash;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, Profile profile) throws Exception {
        Container container = createContainer();
        User user = buildUser(ctx.channel(), profile, container);
        startContainer(user);
        idUserHash.put(user.getProfile().getUserId(), user);
        userHashMap.put(ctx.channel(), user);
        ctx.fireUserEventTriggered(user);
        ctx.pipeline().remove(this);
    }

    private User buildUser(Channel channel, Profile profile, Container container) {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        return new User.UserBuilder()
                .withChannel(channel)
                .withContainer(container)
                .withProfile(profile)
                .withInputStream(in)
                .withOutputStream(out)
                .build();
    }

    private Container createContainer() {
        return new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();
    }

    private void startContainer(User user) {
        Container container = user.getContainer();
        new StartCmd().withDockerClient(dockerClient)
                .withContainer(container)
                .exec();
        String execId = new CreateExecCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
        new StartExecCmd().withStdIn(user.getInputStream())
                .withExecId(execId)
                .withUser(user)
                .withDockerClient(dockerClient)
                .exec();
    }
}

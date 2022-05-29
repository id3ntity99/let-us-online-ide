package com.letus.websocket;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.Client;
import com.letus.docker.command.*;
import com.letus.user.User;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

@ServerEndpoint(value = "/init", configurator = HandShakeConfigurator.class)
public class Websocket {
    private final String ID_TOKEN = "idToken";
    private final HashMap<String, User> tokenUserHash = new HashMap<>();
    private final DockerClient dockerClient = Client.DOCKER_CLIENT.getDockerClient();

    @OnOpen
    public void onOpen(Session session) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        if (!tokenUserHash.containsKey(idToken)) {
            session.setMaxIdleTimeout(1800000);
            try {
                User.UserBuilder userBuilder = new User.UserBuilder()
                        .withInputStream(new PipedInputStream())
                        .withOutputStream(new PipedOutputStream())
                        .withClientSession(session);
                Container container = new CreateCmd().withImage("runner-image")
                        .withDockerClient(dockerClient)
                        .exec();
                new StartCmd().withContainer(container)
                        .withDockerClient(dockerClient)
                        .exec();
                User user = userBuilder.withContainer(container).build();
                String execId = new CreateExecCmd().withContainer(container)
                        .withDockerClient(dockerClient)
                        .exec();
                new StartExecCmd().withUser(user)
                        .withExecId(execId)
                        .withDockerClient(dockerClient)
                        .exec();
                tokenUserHash.put(idToken, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                User user = tokenUserHash.get(idToken);
                user.setClientSession(session);
                Container container = user.getContainer();
                boolean isRunning = Boolean.TRUE.equals(
                        new InspectCmd().withContainer(container)
                                .withDockerClient(dockerClient)
                                .exec()
                                .getState()
                                .getRunning());
                if (!isRunning) {
                    new StartCmd().withDockerClient(dockerClient)
                            .withContainer(container)
                            .exec();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            String idToken = (String) session.getUserProperties().get(ID_TOKEN);
            User user = tokenUserHash.get(idToken);
            PipedOutputStream out = user.getOutputStream();
            out.write(msg.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        User user = tokenUserHash.get(idToken);
        user.close();
    }
}
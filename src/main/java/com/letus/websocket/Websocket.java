package com.letus.websocket;

import com.github.dockerjava.api.model.Container;
import com.letus.command.client.Commands;
import com.letus.user.User;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

@ServerEndpoint(value = "/init", configurator = HandShakeConfigurator.class)
public class Websocket {
    private static final String ID_TOKEN = "idToken";
    private static final HashMap<String, User> sshChannelHashMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        if (!sshChannelHashMap.containsKey(idToken)) {
            session.setMaxIdleTimeout(1800000);
            try {
                // TODO: 아래 코드를 감싸는 파사드 클래스 하나 만들기 (너무 지저분함).
                // TODO: command.client.Commands를 없애고 파사드 클래스를 Command Client로 사용.
                Container container = Commands.createContainerCmd()
                        .withImage("runner-image")
                        .exec()
                        .getContainer();
                Commands.startContainerCmd()
                        .withContainer(container)
                        .exec();
                User user = new User.UserBuilder()
                        .withInputStream(new PipedInputStream())
                        .withOutputStream(new PipedOutputStream())
                        .withContainer(container)
                        .withClientSession(session)
                        .build();
                String execId = Commands.createExecContainerCmd()
                        .withContainer(container)
                        .withUser(user)
                        .exec()
                        .getExecId();
                Commands.startExecContainerCmd()
                        .withExecId(execId)
                        .withUser(user)
                        .exec();
                sshChannelHashMap.put(idToken, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                User user = sshChannelHashMap.get(idToken);
                user.setClientSession(session);
                //TODO 웹소켓 종료 시 세션도 종료되지만, docker exec i/o를 처리하는 스레드는 계속 종료된 세션을 이용하여 통신하므로 이를 해결해야 함.
                Container container = user.getContainer();
                boolean isRunning = Boolean.TRUE.equals(Commands.inspectContainer()
                        .withContainer(container)
                        .exec()
                        .getInspection()
                        .getState()
                        .getRunning());
                if (!isRunning) {
                    Commands.startContainerCmd().withContainer(container).exec();
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
            User user = sshChannelHashMap.get(idToken);
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
        User user = sshChannelHashMap.get(idToken);
        user.close();
    }
}
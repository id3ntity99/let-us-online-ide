package com.letus.websocket;

import com.github.dockerjava.api.model.Container;
import com.letus.command.client.CommandFacade;
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
                // TODO: command.client.Commands를 파사드 클래스로 변경하여 Command Client로 사용.
                Container container = CommandFacade.createContainerCmd("runner-image");
                CommandFacade.startContainerCmd(container);
                User user = new User.UserBuilder().withInputStream(new PipedInputStream())
                        .withOutputStream(new PipedOutputStream())
                        .withContainer(container)
                        .withClientSession(session)
                        .build();
                String execId = CommandFacade.createExecContainerCmd(container);
                CommandFacade.startExecContainerCmd(user, execId);
                sshChannelHashMap.put(idToken, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                User user = sshChannelHashMap.get(idToken);
                user.setClientSession(session);
                Container container = user.getContainer();
                boolean isRunning = Boolean.TRUE.equals(
                        CommandFacade.inspectContainer(container)
                        .getInspection()
                        .getState()
                        .getRunning());
                if (!isRunning) {
                    CommandFacade.startContainerCmd(container);
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
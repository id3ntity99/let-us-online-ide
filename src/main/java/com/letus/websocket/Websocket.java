package com.letus.websocket;

import com.letus.ssh.SSHExecution;
import com.letus.ssh.exceptions.InputTooLargeException;
import com.letus.user.User;
import com.letus.user.UserInfo;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@ServerEndpoint(value = "/init", configurator = HandShakeConfigurator.class)
public class Websocket {
    private static final String ID_TOKEN = "idToken";
    private static final String HOST = "172.17.0.2";
    private static final String USERNAME = "runner";
    private static final String PATH_TO_KNOWN_HOSTS = "/home/yosef/.ssh/known_hosts";
    private static final String PASSWORD = "1234";
    private static final int PORT = 22;
    private static final HashMap<String, User> sshChannelHashMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        if (!sshChannelHashMap.containsKey(idToken)) {
            // UserInfo userInfo = Auth.getInfo(idToken);
            UserInfo userInfo = new UserInfo("id", "email", true, "name", "url");
            session.setMaxIdleTimeout(1800000);
            try {
                User user = new User(userInfo);
                user.setSession(session);
                user.openSSHConnection(USERNAME, HOST, PORT, PASSWORD, PATH_TO_KNOWN_HOSTS);
                sshChannelHashMap.put(idToken, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            User user = sshChannelHashMap.get(idToken);
            user.setSession(session);
            System.out.println("The user already exists");
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        User user = sshChannelHashMap.get(idToken);
        byte[] cmd = msg.getBytes(StandardCharsets.UTF_8);
        try {
            SSHExecution.readExecResult(cmd, user);
        } catch (IOException | InputTooLargeException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String idToken = (String) session.getUserProperties().get(ID_TOKEN);
        User user = sshChannelHashMap.get(idToken);
        try {
            user.getClientSession().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Websocket Session closed");
    }
}
package com.letus.websocket;

import com.jcraft.jsch.JSch;
import com.letus.ssh.SSHExecution;
import com.letus.user.User;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@ServerEndpoint("/init")
public class Websocket {
    private final JSch jsch = new JSch();
    private final String host = "172.17.0.2";
    private final String username = "runner";
    private final String pathToKnownHosts = "/home/yosef/.ssh/known_hosts";
    private final String password = "1234";
    private final int port = 22;
    private static final HashMap<Session, User> sshChannelHashMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        String clientSessionId = session.getId();
        System.out.println("Total threads: " + Thread.activeCount());
        if (!sshChannelHashMap.containsKey(clientSessionId)) {
            session.setMaxIdleTimeout(1800000);
            User user = new User(session);
            user.openSSHConnection(username, host, port, password, pathToKnownHosts);
            sshChannelHashMap.put(session, user);
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) throws Exception {
        User user = sshChannelHashMap.get(session);
        byte[] cmd = msg.getBytes(StandardCharsets.UTF_8);
        SSHExecution.readExecResult(cmd, user);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws IOException {
        User user = sshChannelHashMap.get(session);
        user.closeSSHConnection();
        session.close();
        System.out.println("Session closed");
    }
}
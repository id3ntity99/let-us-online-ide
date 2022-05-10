package com.letus.websocket;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.letus.ssh.SSHConnection;
import com.letus.ssh.SSHExecution;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
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
    private static final HashMap<String, ChannelShell> sshChannelHashMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws Exception {
        String clientSessionId = session.getId();
        System.out.println("Total threads: " + Thread.activeCount());
        if (!sshChannelHashMap.containsKey(clientSessionId)) {
            try {
                session.setMaxIdleTimeout(1800000);
                SSHConnection sshConnThread = new SSHConnection(jsch, username, host, port, password, pathToKnownHosts);
                ChannelShell channel = sshConnThread.getChannel();
                sshChannelHashMap.put(clientSessionId, channel);
                System.out.println("New Session and Channel has been created and stored.");
            } catch (JSchException e) {
                System.out.println(e);
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String msg) throws Exception {
        ChannelShell channel = sshChannelHashMap.get(session.getId());
        System.out.println("Retrieved user's ssh channel");
        byte[] cmd = msg.getBytes(StandardCharsets.UTF_8);
        SSHExecution.readExecResult(cmd, channel, session);
    }
}
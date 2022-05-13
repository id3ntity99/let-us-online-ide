package com.letus.user;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.letus.ssh.SSHConnection;

import javax.websocket.Session;
import java.io.IOException;

public class User {
    private ChannelShell channel;
    private Session clientSession;
    private final SSHConnection conn;
    private final UserInfo userInfo;


    public User(UserInfo userInfo) throws Exception {
        this.conn = new SSHConnection(new JSch());
        this.userInfo = userInfo;
    }

    public void openSSHConnection(String username, String host, int port,
                                  String password, String pathToKnownHostsFile) throws Exception {
        conn.openSSHConnection(username, host, port, password, pathToKnownHostsFile);
        channel = conn.getChannel();
    }

    public void setSession(Session clientSession) {
        this.clientSession = clientSession;
    }

    public void closeSSHConnection() throws IOException {
        conn.closeSSHConnection();
        channel = null;
    }

    public ChannelShell getChannel() {
        return channel;
    }

    public Session getClientSession() {
        return clientSession;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getUserInfoJsonString() {
        return new Gson().toJson(userInfo);
    }
}

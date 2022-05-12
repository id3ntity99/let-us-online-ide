package com.letus.user;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.letus.ssh.SSHConnection;

import javax.websocket.Session;
import java.io.IOException;

public class User {
    private ChannelShell channel;
    private final Session clientSession;
    private final SSHConnection conn;

    public User(Session clientSession) throws Exception{
        this.clientSession = clientSession;
        this.conn = new SSHConnection(new JSch());
    }

    public void openSSHConnection(String username, String host, int port,
                                  String password, String pathToKnownHostsFile) throws Exception {
        conn.openSSHConnection(username, host, port, password, pathToKnownHostsFile);
        channel = conn.getChannel();
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
}

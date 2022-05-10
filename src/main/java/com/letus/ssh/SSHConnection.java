package com.letus.ssh;

import com.jcraft.jsch.*;

import java.io.IOException;

public class SSHConnection {
    private final JSch ssh;
    private Session session;
    private ChannelShell channel;
    private final String username;
    private final String host;
    private final int port;
    private final String password;
    private final String pathToKnownHosts;

    public SSHConnection(JSch ssh, String username, String host, int port,
                         String password, String pathToKnownHosts) throws Exception {
        this.ssh = ssh;
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
        this.pathToKnownHosts = pathToKnownHosts;
        openNewSession(username, host, port, password, pathToKnownHosts);
        openChannel();

    }

    private void openNewSession(String username, String host, int port,
                                String password, String pathToKnownHostsFile) throws Exception {
        ssh.setKnownHosts(pathToKnownHostsFile);
        session = ssh.getSession(username, host, port);
        session.setPassword(password);
        session.setTimeout(1800000);
        //session.noMoreSessionChannels();
        session.connect();
    }

    private void openChannel() throws JSchException{
        channel = (ChannelShell) session.openChannel("shell");
        channel.connect();

    }

    public ChannelShell getChannel() throws IOException{
        if (channel == null) {
            throw new IOException("Channel is null");
        }
        return channel;
    }

    public void closeSession() {
        session.disconnect();
    }
}

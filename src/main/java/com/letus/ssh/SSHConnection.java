package com.letus.ssh;

import com.jcraft.jsch.*;

import java.io.IOException;

public class SSHConnection {
    private final JSch ssh;
    private Session session;
    private ChannelShell channel;

    public SSHConnection(JSch ssh) throws Exception {
        this.ssh = ssh;
    }

    private void openNewSession(String username, String host, int port,
                                String password, String pathToKnownHostsFile) throws Exception {
        ssh.setKnownHosts(pathToKnownHostsFile);
        session = ssh.getSession(username, host, port);
        session.setPassword(password);
        session.setTimeout(1800000);
        session.connect();
    }

    private void openChannel() throws Exception {
        channel = (ChannelShell) session.openChannel("shell");
        channel.connect();
        session.noMoreSessionChannels();

    }

    public void openSSHConnection(String username, String host, int port,
                                  String password, String pathToKnownHostsFile) throws Exception {
        openNewSession(username, host, port, password, pathToKnownHostsFile);
        openChannel();

    }

    public ChannelShell getChannel() throws IOException {
        if (channel == null) {
            throw new IOException("Channel is null");
        }
        return channel;
    }

    public void closeSSHConnection() throws IOException {
        session.disconnect();
        channel.disconnect();
        channel.getInputStream().close();
        channel.getOutputStream().close();
    }
}

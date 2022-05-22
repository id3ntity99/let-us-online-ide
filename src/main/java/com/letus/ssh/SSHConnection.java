package com.letus.ssh;

import com.jcraft.jsch.*;
import com.letus.command.client.Commands;
import com.letus.user.User;

import java.io.IOException;
import java.util.Properties;

/*
* @deprecated Use Docker-exec command instead of SSH connection.
* */
@Deprecated
public class SSHConnection {
    private static final String USERNAME = "runner";
    private static final String PASSWORD = "1234";
    private static final int PORT = 22;
    private static final String PATH_TO_KNOWN_HOSTS = "/home/yosef/.ssh/known_hosts";
    private final JSch ssh = new JSch();
    private Session session;
    private ChannelShell channel;
    private User user;

    public SSHConnection(User user) {
        this.user = user;
    }

    public void openNewSession() throws Exception {
        String ipAddress = Commands.inspectContainerNetworkCmd()
                .withContainer(user.getContainer())
                .exec()
                .getNetworkMap()
                .get("Bridge")
                .getIpAddress();
        ssh.setKnownHosts(PATH_TO_KNOWN_HOSTS);
        session = ssh.getSession(USERNAME, ipAddress, PORT);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(PASSWORD);
        session.setTimeout(1800000);
        session.connect();
    }

    public void openChannel() throws Exception {
        channel = (ChannelShell) session.openChannel("shell");
        channel.connect();
        session.noMoreSessionChannels();
        //user.setChannel(channel);
    }

    public void openSSHConnection() throws Exception {
        openNewSession();
        openChannel();

    }

    public void closeSSHConnection() throws IOException {
        session.disconnect();
        channel.disconnect();
        channel.getInputStream().close();
        channel.getOutputStream().close();
    }
}

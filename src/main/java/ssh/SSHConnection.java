package ssh;

import com.jcraft.jsch.*;

public class SSHConnection extends Thread {
    private final JSch ssh;
    private Session session;
    private static final System.Logger LOGGER = System.getLogger(SSHConnection.class.getName());
    private final String username;
    private final String host;
    private final int port;
    private final String password;
    private final String pathToKnownHosts;

    public SSHConnection(JSch ssh, String username, String host, int port,
                         String password, String pathToKnownHosts) {
        this.ssh = ssh;
        this.username = username;
        this.host = host;
        this.port = port;
        this.password = password;
        this.pathToKnownHosts = pathToKnownHosts;
    }

    private void openNewSession(String username, String host, int port,
                                String password, String pathToKnownHostsFile) throws JSchException {
        ssh.setKnownHosts(pathToKnownHostsFile);
        session = ssh.getSession(username, host, port);
        session.setPassword(password);
        session.setTimeout(1800000);
        session.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void closeSession() {
        session.disconnect();
        LOGGER.log(System.Logger.Level.INFO, "Closing SSH connection");
    }

    @Override
    public void run() {
        try {
            openNewSession(username, host, port, password, pathToKnownHosts);
        } catch (JSchException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }
}

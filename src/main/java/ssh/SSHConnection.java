package ssh;

import com.jcraft.jsch.*;

/**
 * Class that accept client's message (mostly the shell commands) via WebSocket
 * and returns the results of Secure Shell execution.
 * This class uses OutputStream for accepting client's message and
 * InputStream for forwarding the results of Secure Shell execution.
 *
 * @author id3ntity99
 */

public class SSHConnection {
    private final JSch ssh;
    private Session session;
    private ChannelShell channel;
    private static final System.Logger LOGGER = System.getLogger(SSHConnection.class.getName());

    /**
     * Constructor for ssh.SSHandler.
     * This constructor only creates single JSch instance.
     */
    public SSHConnection() {
        this.ssh = new JSch();
    }

    /**
     * Open new session to an SSH server.
     * host and port parameters for specifying target SSH server.
     * username and password for logging in as a specific user.
     * pathToKnownHostsFile is a path to known_hosts file for
     * client's authentication to the target SSH server.
     *
     * @param username             username for logging into target host
     * @param host                 host name of target host
     * @param port                 port number of target host
     * @param password             password for logging into target host
     * @param pathToKnownHostsFile Path to known_hosts file for client's authentication to the target server
     */
    public void openNewSession(String username, String host, int port,
                               String password, String pathToKnownHostsFile) {
        try {
            ssh.setKnownHosts(pathToKnownHostsFile);
            session = ssh.getSession(username, host, port);
            session.setPassword(password);
            session.connect();
        } catch (JSchException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }

    protected ChannelShell getChannelShell() throws NullPointerException{
            if (channel != null) {
                return channel;
            }
            throw new NullPointerException("inputStream is null");
    }

    /**
     * Open a channel between SSH client and SSH server.
     * This channel will send data to and receive data from the SSH server.
     * By invoking this method, the channel's input and output stream will be obtained.
     * The channel uses Shell mode not execution mode.
     */
    public void openChannel() {
        try {
            channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            channel.connect();
        } catch (JSchException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }

    /**
     * Close currently connected SSH session and channel
     */
    protected void closeConnection() {
        channel.disconnect();
        session.disconnect();
        LOGGER.log(System.Logger.Level.INFO, "Closing SSH connection");
    }
}

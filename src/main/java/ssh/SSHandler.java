package ssh;

import com.jcraft.jsch.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Class that accept client's message (mostly the shell commands) via WebSocket
 * and returns the results of Secure Shell execution.
 * This class uses OutputStream for accepting client's message and
 * InputStream for forwarding the results of Secure Shell execution.
 *
 * @author id3ntity99
 */

public class SSHandler {
    private final JSch ssh;
    private Session session;
    private ChannelShell channel;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static final System.Logger LOGGER = System.getLogger(SSHandler.class.getName());

    /**
     * Constructor for ssh.SSHandler.
     * This constructor only creates single JSch instance.
     */
    public SSHandler() {
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
            inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect();
        } catch (JSchException | IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }

    /**
     * Execute remote shell command.
     * This method takes Shell command as a parameter from the websocket.
     * The command received will then be pushed into channel's output stream.
     *
     * @param cmd Shell command to execute remotely.
     */
    public void executeRemoteShell(String cmd) {
        try {
            outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }


    /**
     * Read the result of shell execution from the channel's input stream.
     * The data from the input stream will then be stored in the byte array, whose size is 8192.
     * After writing into the byte array, it will be returned as a result of method calling.
     *
     * @return byte[], result of remote shell execution.
     */
    private byte[] readInputStream() {
        byte[] byteBuffer = new byte[8192];
        try {
            inputStream.read(byteBuffer, 0, byteBuffer.length);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
        return byteBuffer;
    }


    /**
     * Listen to the channel's input stream.
     * If the input stream has bytes to be read, this method will
     * read data from it until there are nothing to read from the input stream.
     */
    public void run() {
        try {
            Thread.sleep(100);
            while (channel.isConnected() && inputStream.available() >= 0) {
                byte[] byteOutput = readInputStream();
                System.out.print(new String(byteOutput, StandardCharsets.UTF_8));
            }
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        }
    }
}

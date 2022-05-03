package ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import ssh.exceptions.InputTooLargeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final System.Logger LOGGER = System.getLogger(Main.class.getName());

    public static void main(String[] args) {
        final String host = "172.17.0.2";
        final String username = "runner";
        final String pathToKnownHostsFile = "/home/yosef/.ssh/known_hosts";
        final String password = "1234";
        final int port = 22;
        final byte[] cmd = "python3 test.py\n".getBytes(StandardCharsets.UTF_8);
        final JSch jsch = new JSch();

        try {
            SSHConnection ssh = new SSHConnection(jsch, username, host, port, password, pathToKnownHostsFile);
            SSHExecution execution = new SSHExecution(ssh.getChannelShell());
            execution.executeRemoteShell(cmd);
            execution.run();
            ssh.closeConnection();
        } catch (JSchException | IOException | InputTooLargeException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
            Thread.currentThread().interrupt();
        }
    }
}
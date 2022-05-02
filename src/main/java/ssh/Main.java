package ssh;

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

        SSHConnection ssh = new SSHConnection();
        ssh.openNewSession(username, host, port, password, pathToKnownHostsFile);
        ssh.openChannel();
        try {
            SSHExecution execution = new SSHExecution(ssh.getChannelShell());
            execution.executeRemoteShell(cmd);
            execution.run();
        } catch (IOException | InputTooLargeException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.ERROR, e);
            Thread.currentThread().interrupt();
        }
        ssh.closeConnection();
    }
}
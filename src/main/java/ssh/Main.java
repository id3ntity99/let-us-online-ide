package ssh;

import java.io.IOException;

public class Main {
    private static final System.Logger LOGGER = System.getLogger(Main.class.getName());

    public static void main(String[] args) {
        final String host = "172.17.0.2";
        final String username = "runner";
        final String pathToKnownHostsFile = "/home/yosef/.ssh/known_hosts";
        final String password = "1234";
        final int port = 22;
        SSHConnection ssh = new SSHConnection();
        ssh.openNewSession(username, host, port, password, pathToKnownHostsFile);
        ssh.openChannel();
        SSHExecution execution = new SSHExecution(ssh.getChannelShell());
        execution.executeRemoteShell("python3 test.py\n");
        execution.run();
    }
}
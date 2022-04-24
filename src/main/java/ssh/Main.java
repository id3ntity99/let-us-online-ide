package ssh;

public class Main {
    private static final System.Logger LOGGER = System.getLogger(Main.class.getName());
    public static void main(String[] args) {
        final String host = "172.17.0.2";
        final String username = "runner";
        final String pathToKnownHostsFile = "/home/yosef/.ssh/known_hosts";
        final String password = "1234";
        final int port = 22;

        SSHandler ssh = new SSHandler();
        ssh.openNewSession(username, host, port, password, pathToKnownHostsFile);
        ssh.openChannel();
        ssh.executeRemoteShell("python3 test.py\n");
        ssh.run();
    }
}
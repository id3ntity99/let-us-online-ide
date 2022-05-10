package ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import ssh.exceptions.InputTooLargeException;

import java.io.IOException;

public class Main {
    private static final System.Logger LOGGER = System.getLogger(Main.class.getName());

    public static void main(String[] args) {
        final String host = "172.17.0.2";
        final String username = "runner";
        final String pathToKnownHostsFile = "/home/yosef/.ssh/known_hosts";
        final String password = "1234";
        final int port = 22;
        final String cmd = "python3 test.py\n";
        final JSch jsch = new JSch();
    }
}
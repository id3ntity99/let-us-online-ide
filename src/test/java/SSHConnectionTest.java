import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ssh.SSHConnection;

import static org.mockito.Mockito.*;

class SSHConnectionTest {
    private SSHConnection conn;
    private final String USER_NAME = "user1";
    private final String HOST = "host1";
    private final int PORT = 22;
    private final String PASSWORD = "password";
    private final String KNOWN_HOSTS_PATH = "/some/path";

    private final JSch MOCKED_JSCH = mock(JSch.class);
    private final Session MOCKED_SESSION = mock(Session.class);

    @BeforeEach
    void setUp() throws JSchException{
        when(MOCKED_JSCH.getSession(USER_NAME, HOST, PORT)).thenReturn(MOCKED_SESSION);
        conn = new SSHConnection(MOCKED_JSCH, USER_NAME, HOST, PORT, PASSWORD, KNOWN_HOSTS_PATH);
        conn.run();
    }

    @Test
    void testInitialization() throws JSchException {
        verify(MOCKED_JSCH).setKnownHosts(KNOWN_HOSTS_PATH);
        verify(MOCKED_SESSION).setPassword(PASSWORD);
        verify(MOCKED_SESSION).connect();
    }

    @Test
    void testGetSession() {
        Session session = conn.getSession();
        Assertions.assertInstanceOf(Session.class, session);
    }

    @Test
    void testCloseConnection() {
        conn.closeSession();
        verify(MOCKED_SESSION).disconnect();
    }
}
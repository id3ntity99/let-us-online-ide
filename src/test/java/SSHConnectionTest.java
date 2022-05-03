import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ssh.SSHConnection;

import static org.mockito.Mockito.*;

public class SSHConnectionTest {
    private SSHConnection conn;
    private final String USER_NAME = "user1";
    private final String HOST = "host1";
    private final int PORT = 22;
    private final String PASSWORD = "password";
    private final String KNOWN_HOSTS_PATH = "/some/path";

    private final JSch MOCKED_JSCH = mock(JSch.class);
    private final Session MOCKED_SESSION = mock(Session.class);
    private final ChannelShell MOCKED_CHANNEL = mock(ChannelShell.class);

    @BeforeEach
    void setUp() throws JSchException{
        when(MOCKED_JSCH.getSession(USER_NAME, HOST, PORT)).thenReturn(MOCKED_SESSION);
        when(MOCKED_SESSION.openChannel(anyString())).thenReturn(MOCKED_CHANNEL);
        conn = new SSHConnection(MOCKED_JSCH, USER_NAME, HOST, PORT, PASSWORD, KNOWN_HOSTS_PATH);
    }

    @Test
    void testInitialization() throws JSchException {
        verify(MOCKED_JSCH).setKnownHosts(KNOWN_HOSTS_PATH);
        verify(MOCKED_SESSION).setPassword(PASSWORD);
        verify(MOCKED_SESSION).connect();
        verify(MOCKED_CHANNEL).setPty(anyBoolean());
        verify(MOCKED_CHANNEL).connect();
    }

    @Test
    void testGetChannelShell() throws NullPointerException{
        ChannelShell channel = conn.getChannelShell();
        Assertions.assertInstanceOf(ChannelShell.class, channel);
    }

    @Test
    void testCloseConnection() {
        conn.closeConnection();
        verify(MOCKED_CHANNEL).disconnect();
        verify(MOCKED_SESSION).disconnect();
    }
}
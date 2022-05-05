import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.jupiter.api.*;
import org.mockito.*;
import ssh.SSHExecution;
import ssh.exceptions.InputTooLargeException;

import javax.websocket.RemoteEndpoint;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class SSHExecutionTest {
    private SSHExecution sshExec;
    @Spy
    private static final javax.websocket.Session SPIED_CLIENT_SESSION = spy(javax.websocket.Session.class);
    @Spy
    private static final RemoteEndpoint.Basic MOCKED_CLIENT_ENDPOINT = spy(RemoteEndpoint.Basic.class);
    private static final ChannelShell MOCKED_CHANNEL = mock(ChannelShell.class);

    private static final Session MOCKED_SESSION = mock(Session.class);
    @Spy
    private static final PipedInputStream PIPED_IN = spy(new PipedInputStream(2048));
    @Spy
    private static final PipedOutputStream PIPED_OUT = spy(PipedOutputStream.class);


    @BeforeAll
    public static void setup() throws IOException {
        when(MOCKED_CHANNEL.getInputStream()).thenReturn(PIPED_IN);
        when(MOCKED_CHANNEL.getOutputStream()).thenReturn(PIPED_OUT);
        when(SPIED_CLIENT_SESSION.getBasicRemote()).thenReturn(MOCKED_CLIENT_ENDPOINT);
        PIPED_IN.connect(PIPED_OUT);
    }

    @BeforeEach
    void initSSHExec() throws IOException, JSchException {
        when(MOCKED_SESSION.openChannel(anyString())).thenReturn(MOCKED_CHANNEL);
        sshExec = new SSHExecution(MOCKED_SESSION);
    }

    @AfterAll
    public static void release() throws IOException {
        PIPED_IN.close();
        PIPED_OUT.close();
    }


    @Test
    void constructorTest() throws IOException {
        verify(MOCKED_CHANNEL).getOutputStream();
        verify(MOCKED_CHANNEL).getInputStream();
    }

    @Test
    void testRunWhenChannelIsDisconnectedTest() throws Exception {
        String cmd = "ls";
        byte[] bytesCmd = cmd.getBytes(StandardCharsets.UTF_8);
        when(MOCKED_CHANNEL.isConnected()).thenReturn(false);
        doReturn(1).when(PIPED_IN).available();
        Assertions.assertThrows(IOException.class, () -> sshExec.execute(cmd, SPIED_CLIENT_SESSION));
        byte[] buffer = new byte[2];
        PIPED_IN.read(buffer);
        Assertions.assertArrayEquals(bytesCmd, buffer);
        reset(PIPED_IN);
    }

    @Test
    void testRunWhenInputStreamIsEmptyTest() throws IOException {
        String cmd = "ls";
        byte[] bytesCmd = cmd.getBytes(StandardCharsets.UTF_8);
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        doReturn(0).when(PIPED_IN).available();
        Assertions.assertThrows(IOException.class, () -> sshExec.execute(cmd, SPIED_CLIENT_SESSION));
        byte[] buffer = new byte[2];
        PIPED_IN.read(buffer);
        Assertions.assertArrayEquals(bytesCmd, buffer);
        reset(PIPED_IN);
    }

    @Test
    void testRunWithSmallerInput() throws Exception {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        String validCmd = "date\n";
        byte[] bytesValidCmd = validCmd.getBytes(StandardCharsets.UTF_8);
        doAnswer(invocation -> {
            ByteBuffer arg0 = invocation.getArgument(0);
            byte[] remaining = new byte[arg0.remaining()];
            arg0.get(remaining);
            Assertions.assertArrayEquals(bytesValidCmd, remaining);
            return null;
        }).when(MOCKED_CLIENT_ENDPOINT).sendBinary(any(ByteBuffer.class));
        sshExec.execute(validCmd, SPIED_CLIENT_SESSION);
        reset(MOCKED_CLIENT_ENDPOINT);

    }


    @Test
    void testRunWithProperInput() throws Exception {
        String randomString = new String(new char[512]).replace('\0', 'a');
        byte[] randomBytes = randomString.getBytes(StandardCharsets.UTF_8);
        doAnswer(invocation -> {
            ByteBuffer arg0 = invocation.getArgument(0);
            byte[] remaining = new byte[arg0.remaining()];
            arg0.get(remaining);
            Assertions.assertArrayEquals(randomBytes, remaining);
            return null;
        }).when(MOCKED_CLIENT_ENDPOINT).sendBinary(any(ByteBuffer.class));
        sshExec.execute(randomString, SPIED_CLIENT_SESSION);
        reset(MOCKED_CLIENT_ENDPOINT);
    }

    @Test
    void testRunWithEqualOrLargerInput() {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        String randomString = new String(new char[2001]).replace('\0', 'a');
        Assertions.assertThrows(InputTooLargeException.class,
                () -> sshExec.execute(randomString, SPIED_CLIENT_SESSION));
    }
}
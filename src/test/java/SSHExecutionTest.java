import com.jcraft.jsch.ChannelShell;
import org.junit.jupiter.api.*;
import org.mockito.*;
import ssh.SSHExecution;
import ssh.exceptions.InputTooLargeException;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class SSHExecutionTest {
    private SSHExecution sshExec;
    private static final ChannelShell MOCKED_CHANNEL = mock(ChannelShell.class);
    @Spy
    private static final PipedInputStream PIPED_IN = spy(new PipedInputStream(2048));
    @Spy
    private static final PipedOutputStream PIPED_OUT = spy(PipedOutputStream.class);


    @BeforeAll
    public static void setup() throws IOException {
        when(MOCKED_CHANNEL.getInputStream()).thenReturn(PIPED_IN);
        when(MOCKED_CHANNEL.getOutputStream()).thenReturn(PIPED_OUT);
        PIPED_IN.connect(PIPED_OUT);
    }

    @BeforeEach
    void initSSHExec() throws IOException {
        sshExec = new SSHExecution(MOCKED_CHANNEL);
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
    @DisplayName("executeRemoteShell_WhenWorkingProperly_True")
    void executeRemoteShellTest() throws IOException, InputTooLargeException {
        String inputCmd = "ls";
        byte[] byteInputCmd = inputCmd.getBytes(StandardCharsets.UTF_8);
        int returnedBytesLen = sshExec.executeRemoteShell(byteInputCmd);
        byte[] buffer = new byte[byteInputCmd.length];

        PIPED_IN.read(buffer, 0, byteInputCmd.length);

        Assertions.assertEquals(byteInputCmd.length, returnedBytesLen);
        Assertions.assertArrayEquals(byteInputCmd, buffer);
    }

    @Test
    void executeRemoteShellInputTooLarge() {
        byte[] randomBytes = new String(new char[2001])
                .replace('\0', 'a')
                .getBytes(StandardCharsets.UTF_8);
        Assertions.assertThrows(InputTooLargeException.class,
                () -> sshExec.executeRemoteShell(randomBytes));
    }

    @Test
    @DisplayName("executeRemoteShell_WhenExceptionRaised_True")
    void executeRemoteShellExceptionTest() throws IOException {
        byte[] cmdBytes = "date\n".getBytes(StandardCharsets.UTF_8);
        doThrow(IOException.class)
                .when(PIPED_OUT)
                .write(ArgumentMatchers.any(byte[].class)); // OutputStream.write()을 호출하면 IOException 을 발생.
        Assertions.assertThrows(IOException.class, () -> sshExec.executeRemoteShell(cmdBytes));
        reset(PIPED_OUT);
    }


    @Test
    void testRunWhenChannelIsDisconnectedTest() throws Exception {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(false);
        doReturn(1).when(PIPED_IN).available();
        Assertions.assertThrows(IOException.class, () -> sshExec.run());
        reset(PIPED_IN);
    }

    @Test
    void testRunWhenInputStreamIsEmptyTest() throws IOException {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        doReturn(0).when(PIPED_IN).available();
        Assertions.assertThrows(IOException.class, () -> sshExec.run());
        reset(PIPED_IN);
    }

    @Test
    void testRunWithSmallerInput() throws Exception {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        String validCmd = "date\n";
        byte[] bytesValidCmd = validCmd.getBytes(StandardCharsets.UTF_8);
        sshExec.executeRemoteShell(bytesValidCmd);
        byte[] result = sshExec.run();
        Assertions.assertArrayEquals(bytesValidCmd, result);
    }

    @Test
    void testRunWithEqualOrLargerInput() throws Exception {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        String randomString = new String(new char[512]).replace('\0', 'a');
        byte[] randomBytes = randomString.getBytes(StandardCharsets.UTF_8);
        sshExec.executeRemoteShell(randomBytes);
        byte[] result = sshExec.run();
        Assertions.assertArrayEquals(randomBytes, result);
    }
}

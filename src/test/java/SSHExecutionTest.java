import com.jcraft.jsch.ChannelShell;
import org.junit.jupiter.api.*;
import org.mockito.*;
import ssh.SSHExecution;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class SSHExecutionTest {
    private SSHExecution sshExec;
    // private AutoCloseable closeable;
    private static final ChannelShell MOCKED_CHANNEL = mock(ChannelShell.class);
    @Spy
    private static final PipedInputStream PIPED_IN = spy(PipedInputStream.class);
    @Spy
    private static final PipedOutputStream PIPED_OUT = spy(PipedOutputStream.class);


    @BeforeAll
    public static void setup() throws IOException {
        when(MOCKED_CHANNEL.getInputStream()).thenReturn(PIPED_IN);
        when(MOCKED_CHANNEL.getOutputStream()).thenReturn(PIPED_OUT);
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

    // @Captor
    // ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);


    @Test
    void constructorTest() throws IOException {
        verify(MOCKED_CHANNEL).getOutputStream();
        verify(MOCKED_CHANNEL).getInputStream();
    }


    @Test
    @DisplayName("executeRemoteShell_WhenWorkingProperly_True")
    void executeRemoteShellTest() throws IOException {
        PIPED_IN.connect(PIPED_OUT);
        String inputCmd = "ls";
        byte[] byteInputCmd = inputCmd.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[byteInputCmd.length];

        sshExec.executeRemoteShell(inputCmd);

        PIPED_IN.read(buffer, 0, buffer.length);
        Assertions.assertArrayEquals(byteInputCmd, buffer);
    }

    @Test
    @DisplayName("executeRemoteShell_WhenExceptionRaised_True")
    void executeRemoteShellExceptionTest() throws IOException {
        doThrow(IOException.class)
                .when(PIPED_OUT)
                .write(ArgumentMatchers.any(byte[].class)); // OutputStream.write()을 호출하면 IOException 을 발생.
        Assertions.assertThrows(IOException.class, () -> sshExec.executeRemoteShell("date\n"));
    }


    @Test
    void runSSHWhenChannelIsDisconnectedTest() throws Exception {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(false);
        when(PIPED_IN.available()).thenReturn(1);
        Assertions.assertThrows(IOException.class, () -> sshExec.run());
        reset(PIPED_IN);
    }

    @Test
    void runSSHWhenInputStreamIsEmptyTest() throws IOException {
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        when(PIPED_IN.available()).thenReturn(0);
        Assertions.assertThrows(IOException.class, () -> sshExec.run());
        reset(PIPED_IN);
    }

    @Test
    void testRunWithSmallerInputThanBuffer() throws Exception{
        when(MOCKED_CHANNEL.isConnected()).thenReturn(true);
        String validCmd = "date\n";
        byte[] bytesValidCmd = validCmd.getBytes(StandardCharsets.UTF_8);
        PIPED_OUT.write(bytesValidCmd, 0, bytesValidCmd.length);
        byte[] result = sshExec.run();
        Assertions.assertArrayEquals(bytesValidCmd, result);
    }
}

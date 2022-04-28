import com.jcraft.jsch.ChannelShell;
import org.junit.jupiter.api.*;
import ssh.SSHExecution;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.*;

class SSHExecutionTest {
    SSHExecution sshExec;
    private static final InputStream tempInputStream = mock(InputStream.class);
    private static final OutputStream tempOutputStream = mock(OutputStream.class);
    private static final ChannelShell mockedChannel = mock(ChannelShell.class);

    @BeforeAll
    public static void setup() throws IOException {
        when(mockedChannel.getInputStream()).thenReturn(tempInputStream);
        when(mockedChannel.getOutputStream()).thenReturn(tempOutputStream);
    }


    @Test
    void constructorTest() throws IOException{
        sshExec = new SSHExecution(mockedChannel);
        verify(mockedChannel).getOutputStream();
        verify(mockedChannel).getInputStream();
    }
}

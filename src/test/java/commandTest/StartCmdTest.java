package commandTest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.*;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StartCmdTest extends CommandsTest {
    final Logger startCmdLogger = context.getLogger(StartCmd.class);
    @Spy
    final StartContainerCmd spyStartCmd = spy(spyClient.startContainerCmd(anyString()));

    @Test
    void startContainerTest() {
        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();
        new StartCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
        InspectContainerResponse res = new InspectCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
        assertTrue(res.getState().getRunning());

        new StopCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();

        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Test
    void startContainerNotFoundExceptionTest() {
        startCmdLogger.addAppender(listAppender);

        when(spyClient.startContainerCmd(anyString())).thenReturn(spyStartCmd);
        doThrow(NotFoundException.class).when(spyStartCmd).exec();

        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();
        new StartCmd().withDockerClient(spyClient)
                .withContainer(container)
                .exec();

        assertEquals(Level.ERROR, listAppender.list.get(0).getLevel());

        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Test
    void startContainerNullTest() {
        assertThrows(NullPointerException.class, new StartCmd()::exec);
    }
}

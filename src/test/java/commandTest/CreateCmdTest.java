package commandTest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.CreateCmd;
import com.letus.docker.command.RemoveCmd;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CreateCmdTest extends CommandsTest {
    final Logger createLogger = context.getLogger(CreateCmd.class);
    @Spy
    final CreateContainerCmd spyCreateCmd = spy(spyClient.createContainerCmd(anyString()));

    @Test
    void createContainerTest() {
        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();
        assertFalse(container.getId().isEmpty());
        // FIXME
        //      RemoveCmd assertion.
        new RemoveCmd().withDockerClient(dockerClient)
                .withContainer(container)
                .exec();
    }

    @Test
    void createContainerNotFoundExceptionTest() {
        createLogger.addAppender(listAppender);

        when(spyClient.createContainerCmd(anyString())).thenReturn(spyCreateCmd);
        doThrow(NotFoundException.class).when(spyCreateCmd).exec();

        Container container = new CreateCmd().withDockerClient(spyClient)
                .withImage("runner-image")
                .exec();

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        logsList.clear();
    }

    @Test
    void createContainerNullTest() {
        assertThrows(NullPointerException.class, new CreateCmd()::exec);
    }
}

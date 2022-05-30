package commandTest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.CreateCmd;
import com.letus.docker.command.InspectCmd;
import com.letus.docker.command.RemoveCmd;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class InspectCmdTest extends CommandsTest {
    final Logger inspectCmdLogger = context.getLogger(InspectCmd.class);
    @Spy
    final InspectContainerCmd spyInspectCmd = spy(spyClient.inspectContainerCmd(anyString()));

    @Test
    void inspectContainerTest() {
        Container container = new CreateCmd().withDockerClient(dockerClient)
                .withImage("runner-image")
                .exec();
        InspectContainerResponse res = new InspectCmd().withContainer(container).withDockerClient(dockerClient).exec();
        assertFalse(res.getId().isEmpty());
        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Test
    void inspectContainerNotFoundExceptionTest() {
        inspectCmdLogger.addAppender(listAppender);
        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();

        when(spyClient.inspectContainerCmd(anyString())).thenReturn(spyInspectCmd);
        doThrow(NotFoundException.class).when(spyInspectCmd).exec();

        new InspectCmd().withContainer(container)
                .withDockerClient(spyClient)
                .exec();
        assertEquals(Level.ERROR, listAppender.list.get(0).getLevel());
        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
        listAppender.list.clear();
    }

    @Test
    void inspectContainerNullTest() {
        assertThrows(NullPointerException.class, new InspectCmd()::exec);
    }

}

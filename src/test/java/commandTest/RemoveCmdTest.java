package commandTest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.CreateCmd;
import com.letus.docker.command.RemoveCmd;
import com.letus.docker.command.StartCmd;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RemoveCmdTest extends CommandsTest {
    @Spy
    RemoveContainerCmd spyRemoveCmd = spy(spyClient.removeContainerCmd(anyString()));
    final Logger removeCmdLogger = context.getLogger(RemoveCmd.class);
    //TODO 컨테이너를 멈추지않고 삭제하면 어떻게 되는지 확인하기.

    @Test
    void removeContainerTest() {
        removeCmdLogger.addAppender(listAppender);

        Container container = new CreateCmd().withDockerClient(dockerClient)
                .withImage(IMAGE)
                .exec();

        Void result = new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
        assertTrue(listAppender.list.isEmpty());
        assertNull(result);
    }

    @Test
    void removeContainerNotFoundExceptionTest() {
        when(spyClient.removeContainerCmd(anyString())).thenReturn(spyRemoveCmd);
        doThrow(NotFoundException.class).when(spyRemoveCmd).exec();

        Container container = new CreateCmd().withDockerClient(dockerClient)
                .withImage(IMAGE)
                .exec();
        RemoveCmd cmd = new RemoveCmd().withContainer(container)
                .withDockerClient(spyClient);

        assertThrows(NotFoundException.class, cmd::exec);

        new RemoveCmd().withDockerClient(dockerClient)
                .withContainer(container)
                .exec();
    }

    @Test
    void removeRunningContainerTest() {
        removeCmdLogger.addAppender(listAppender);
        Container container = new CreateCmd().withImage(IMAGE)
                .withDockerClient(dockerClient)
                .exec();

        new StartCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();

        new RemoveCmd().withContainer(container)
                .withDockerClient(spyClient)
                .exec();
        assertEquals(Level.DEBUG, listAppender.list.get(0).getLevel());
        listAppender.list.forEach(i -> System.out.println(i));
        listAppender.list.clear();
    }

    @Test
    void removeContainerNullTest() {
        assertThrows(NullPointerException.class, new RemoveCmd()::exec);
    }
}

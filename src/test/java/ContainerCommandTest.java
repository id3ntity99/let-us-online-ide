import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import com.letus.docker.Client;
import com.letus.docker.command.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContainerCommandTest {
    final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    final static ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    @Spy
    final DockerClient spyClient = spy(DockerClientBuilder.getInstance().build());
    final DockerClient dockerClient = Client.DOCKER_CLIENT.getDockerClient();

    @BeforeAll
    public static void init() {
        listAppender.start();
    }

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
        Logger createLogger = context.getLogger(CreateCmd.class);
        createLogger.addAppender(listAppender);
        CreateContainerCmd spyCreateCmd = spy(
                spyClient.createContainerCmd("runner-image")
        );

        when(spyClient.createContainerCmd(anyString())).thenReturn(spyCreateCmd);
        doThrow(NotFoundException.class).when(spyCreateCmd).exec();

        new CreateCmd().withDockerClient(spyClient)
                .withImage("runner-image")
                .exec();

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        logsList.clear();
    }

    @Test
    void inspectContainerTest() {
        Container container = new CreateCmd().withDockerClient(dockerClient)
                .withImage("runner-image")
                .exec();
        InspectContainerResponse res = new InspectCmd().withContainer(container).withDockerClient(dockerClient).exec();
        assertFalse(res.getId().isEmpty());
        new RemoveCmd().withContainer(container).withDockerClient(dockerClient);
    }

    @Test
    void inspectContainerNotFoundExceptionTest() {
        Logger inspectCmdLogger = context.getLogger(InspectCmd.class);
        inspectCmdLogger.addAppender(listAppender);
        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();
        InspectContainerCmd spyInspectCmd = spy(spyClient.inspectContainerCmd(container.getId()));

        when(spyClient.inspectContainerCmd(anyString())).thenReturn(spyInspectCmd);
        doThrow(NotFoundException.class).when(spyInspectCmd).exec();

        new InspectCmd().withContainer(container)
                .withDockerClient(spyClient)
                .exec();
        assertEquals(Level.ERROR, listAppender.list.get(0).getLevel());
        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Test
    void containerStartTest() {
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
}

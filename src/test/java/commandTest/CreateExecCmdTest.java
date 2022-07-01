package commandTest;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateExecCmdTest extends CommandsTest {
    @Test
    void createExecTest() {
        Container container = new CreateCmd().withImage("runner-image")
                .withDockerClient(dockerClient)
                .exec();

        new StartCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();

        String execId = new CreateExecCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();

        assertFalse(execId.isEmpty());

        new StopCmd().withDockerClient(dockerClient)
                .withContainer(container)
                .exec();

        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Test
    void createExecNullTest() {
        assertThrows(NullPointerException.class, new CreateExecCmd()::exec);
    }
}

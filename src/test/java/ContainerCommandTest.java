import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.client.ContainerCommands;
import com.letus.docker.command.response.InspectContainerRes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContainerManagerTest {
    @Test
    void containerCreateTest() {
        Container container = ContainerCommands.create("runner-image");
        Assertions.assertFalse(container.getId().isEmpty());
        boolean isRemoved = ContainerCommands.remove(container);
        Assertions.assertTrue(isRemoved);
    }

    @Test
    void containerInspectTest() {
        Container container = ContainerCommands.create("runner-image");
        InspectContainerRes res = ContainerCommands.inspect(container);
        Assertions.assertFalse(res.getInspection().getId().isEmpty());
        boolean isRemoved = ContainerCommands.remove(container);
        Assertions.assertTrue(isRemoved);
    }
}

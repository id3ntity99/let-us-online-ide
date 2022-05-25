import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.ContainerManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContainerManagerTest {
    @Test
    void containerCreateTest() {
        ContainerManager manager = new ContainerManager();
        Container container = manager.create("runner-image");
        Assertions.assertFalse(container.getId().isEmpty());
        boolean isRemoved = manager.remove(container);
        Assertions.assertTrue(isRemoved);
    }

    @Test
    void containerInspectTest() {
        ContainerManager manager = new ContainerManager();
        Container container = manager.create("runner-image");
        InspectContainerResponse res = manager.inspect(container);
        Assertions.assertFalse(res.getId().isEmpty());
        boolean isRemoved = manager.remove(container);
        Assertions.assertTrue(isRemoved);
    }
}

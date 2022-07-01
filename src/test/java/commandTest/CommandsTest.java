package commandTest;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.letus.docker.Client;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Spy;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.spy;

abstract class CommandsTest {
    protected final static String IMAGE = "runner-image";
    protected final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    protected final static ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    protected final DockerClient dockerClient = Client.DOCKER_CLIENT.getDockerClient();
    @Spy
    protected final DockerClient spyClient = spy(DockerClientBuilder.getInstance().build());

    @BeforeAll
    public static void init() {
        listAppender.start();
    }
}

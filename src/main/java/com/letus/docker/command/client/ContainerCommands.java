package com.letus.docker.command.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.letus.docker.command.*;
import com.letus.docker.command.response.InspectContainerNetworkRes;
import com.letus.docker.command.response.InspectContainerRes;
import com.letus.user.User;

/**
 * An instance of this class plays the role of both an Invoker(, which is part of Command pattern)
 * and a Facade. Technically, this class is an Invoker as well as a Client because
 * it creates Command objects and triggers exec method of the commands.
 * The real Client (the Websocket class) just only need to use this facade object
 * to interact with ContainerManager (the receiver) indirectly.
 * After that, this class returns corresponding concrete Response objects for later uses.
 */
public class ContainerCommands {

    private static final DockerClientConfig config = new DefaultDockerClientConfig.Builder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
    private static final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
    private static final DockerClient dockerClient = DockerClientBuilder.getInstance()
            .withDockerHttpClient(httpClient)
            .build();

    private ContainerCommands() {}

    public static Container create(String image) {
        return new CreateCmd().withImage(image)
                .withDockerClient(dockerClient)
                .exec()
                .getContainer();
    }

    public static Container start(Container container) {
        return new StartCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec()
                .getContainer();
    }

    public static void stop(Container container) {
        new StopCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    @Deprecated
    /**
     * @deprecated Use ContainerCommands.inspect() instead.
     */
    public static InspectContainerNetworkRes inspectNetwork(Container container) {
        return new InspectNetworkCmd().withContainer(container)
                .exec();
    }

    public static InspectContainerRes inspect(Container container) {
        return new InspectCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }

    public static String createExec(Container container) {
        return new CreateExecCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec()
                .getExecId();
    }

    public static void startExec(User user, String execId) {
        new StartExecCmd().withExecId(execId)
                .withDockerClient(dockerClient)
                .withUser(user)
                .exec();
    }

    public static void remove(Container container) {
        new RemoveCmd().withContainer(container)
                .withDockerClient(dockerClient)
                .exec();
    }
}

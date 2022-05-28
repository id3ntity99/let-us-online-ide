package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * This class is responsible for stopping a running container.
 * Example usage: new StopCmd().withDockerClient(client).withContainer(container).exec();
 */
public class StopCmd extends AbstractCommand<StopCmd, Void> {
    @CheckForNull
    Container container;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A docker-api client as a parameter.
     * @return Returns an object with the initialized dockerClient field.
     */
    @Override
    public StopCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    /**
     * A method to initialize container field of the instance.
     *
     * @param container A container object to stop.
     * @return Returns an object with initialized container field.
     */
    public StopCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     * Invocation of this method will stop the running container.
     *
     * @return Returns null;
     */
    @Nullable
    public Void exec() {
        StopContainerCmd cmd = dockerClient.stopContainerCmd(container.getId());
        try {
            cmd.exec();
        } catch (NotModifiedException | NotFoundException e) {
            logger.error("Couldn't stop container...", e);
        }
        return null;
    }
}

package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class RemoveCmd extends AbstractCommand<RemoveCmd, Void> {
    @CheckForNull
    private Container container;

    @Override
    public RemoveCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    public RemoveCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    @Nullable
    public Void exec() throws NotFoundException{
        String containerId = container.getId();
        RemoveContainerCmd cmd = dockerClient.removeContainerCmd(containerId);
        try {
            cmd.withRemoveVolumes(true).exec();
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            logger.debug(String.format("Stopping container: %s", containerId));
            new StopCmd().withContainer(container)
                    .withDockerClient(dockerClient)
                    .exec();
            logger.debug(String.format("Stopped container: %s", containerId));
            logger.debug(String.format("Removing stopped container: %s", containerId));
            cmd.withRemoveVolumes(true).exec();
            logger.debug(String.format("Removed container: %s", containerId));
        }
        return null;
    }
}

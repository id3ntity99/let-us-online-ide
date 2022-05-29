package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveContainerCmd;
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
    public Void exec() {
        String containerId = container.getId();
        RemoveContainerCmd cmd = dockerClient.removeContainerCmd(containerId);
        try {
            cmd.withRemoveVolumes(true).exec();
        } catch (NotFoundException e) {
            logger.error("Couldn't remove container...", e);
        }
        return null;
    }
}

package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.DefaultResponse;

import javax.annotation.Nullable;

public class StopCmd extends AbstractCommand<StopCmd, Void> {
    Container container;

    @Override
    public StopCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    public StopCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    @Nullable
    public Void exec() {
        StopContainerCmd cmd = dockerClient.stopContainerCmd(container.getId());
        cmd.exec();
        return null;
    }
}

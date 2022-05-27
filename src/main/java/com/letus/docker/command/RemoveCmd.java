package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveContainerCmd;
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
        boolean isRemoved = false;
        RemoveContainerCmd cmd = dockerClient.removeContainerCmd(containerId);
        cmd.withRemoveVolumes(true).exec();
        if (search(containerId) == null) { // 컨테이너가 삭제되어 search 되지 않는경우
            isRemoved = true;
        }
        return null;
    }
}

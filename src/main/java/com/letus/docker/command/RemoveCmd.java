package com.letus.docker.command;

import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.DefaultResponse;

import javax.annotation.CheckForNull;

public class RemoveCmd extends AbstractCommand {
    @CheckForNull
    private Container container;

    public RemoveCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public DefaultResponse exec() {
        String containerId = container.getId();
        boolean isRemoved = false;
        RemoveContainerCmd cmd = dockerClient.removeContainerCmd(containerId);
        cmd.withRemoveVolumes(true).exec();
        if (search(containerId) == null) { // 컨테이너가 삭제되어 search 되지 않는경우
            isRemoved = true;
        }
        return new DefaultResponse(isRemoved);
    }
}

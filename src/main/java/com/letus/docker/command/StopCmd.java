package com.letus.docker.command;

import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.client.ContainerCommands;
import com.letus.docker.command.response.DefaultResponse;

public class StopCmd extends AbstractCommand {
    Container container;

    public StopCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public DefaultResponse exec() {
        StopContainerCmd cmd = dockerClient.stopContainerCmd(container.getId());
        cmd.exec();
        Boolean isRunning = ContainerCommands.inspect(container)
                .getInspection()
                .getState()
                .getRunning();
        if (Boolean.TRUE.equals(isRunning)) {
            return new DefaultResponse(false);
        }
        return new DefaultResponse(true);
    }
}

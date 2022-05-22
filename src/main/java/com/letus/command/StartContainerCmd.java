package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.command.response.Response;
import com.letus.command.response.StartContainerRes;
import com.letus.docker.ContainerManager;

public class StartContainerCmd implements Command{
    private final ContainerManager manager = new ContainerManager();
    private Container container;

    public StartContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public Response exec() {
        manager.start(container);
        return new StartContainerRes(container);
    }
}

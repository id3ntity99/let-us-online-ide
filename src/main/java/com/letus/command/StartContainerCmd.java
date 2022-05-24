package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.command.response.StartContainerRes;
import com.letus.docker.ContainerManager;

public class StartContainerCmd implements Command{
    private ContainerManager manager;
    private Container container;

    public StartContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public StartContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }

    public StartContainerRes exec() {
        manager.start(container);
        return new StartContainerRes(container);
    }
}

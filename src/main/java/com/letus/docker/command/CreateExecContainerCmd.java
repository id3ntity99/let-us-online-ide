package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.command.response.CreateExecContainerRes;
import com.letus.docker.ContainerManager;


public class CreateExecContainerCmd implements Command {
    ContainerManager manager;
    Container container;
    String execId;

    public CreateExecContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public CreateExecContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }

    public CreateExecContainerRes exec() {
        execId = manager.createExec(container);
        return new CreateExecContainerRes(execId);
    }
}

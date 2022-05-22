package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.command.response.CreateExecContainerRes;
import com.letus.command.response.Response;
import com.letus.docker.ContainerManager;
import com.letus.user.User;

import javax.annotation.Nonnull;


public class CreateExecContainerCmd implements Command{
    ContainerManager manager = new ContainerManager();
    Container container;

    User user;

    String execId;

    public CreateExecContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public CreateExecContainerCmd withUser(User user) {
        this.user = user;
        return this;
    }

    public CreateExecContainerRes exec() {
        execId = manager.createExec(container, user);
        return new CreateExecContainerRes(execId);
    }
}

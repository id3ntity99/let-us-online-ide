package com.letus.command;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.command.response.InspectContainerRes;
import com.letus.command.response.Response;
import com.letus.docker.ContainerManager;


public class InspectContainerCmd implements Command{
    private ContainerManager manager = new ContainerManager();
    private Container container;

    public InspectContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public InspectContainerRes exec() {
        InspectContainerResponse res = manager.inspect(container);
        return new InspectContainerRes(res);
    }
}

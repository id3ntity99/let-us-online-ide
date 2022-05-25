package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.letus.command.response.InspectContainerNetworkRes;
import com.letus.docker.ContainerManager;

import java.util.Map;

public class InspectContainerNetworkCmd implements Command {
    private Container container;

    private ContainerManager manager;

    public InspectContainerNetworkCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public InspectContainerNetworkCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }

    public InspectContainerNetworkRes exec() {
        Map<String, ContainerNetwork> networkMap = manager.inspect(container).getNetworkSettings().getNetworks();
        return new InspectContainerNetworkRes(networkMap);
    }
}

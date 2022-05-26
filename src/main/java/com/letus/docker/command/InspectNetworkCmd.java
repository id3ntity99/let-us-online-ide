package com.letus.docker.command;

import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.letus.docker.command.response.InspectContainerNetworkRes;

import java.util.Map;

public class InspectNetworkCmd extends AbstractCommand{
    private Container container;

    public InspectNetworkCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    public InspectContainerNetworkRes exec() {
        InspectContainerCmd cmd = dockerClient.inspectContainerCmd(container.getId());
        Map<String, ContainerNetwork> networkMap = cmd.exec().getNetworkSettings().getNetworks();
        return new InspectContainerNetworkRes(networkMap);
    }
}

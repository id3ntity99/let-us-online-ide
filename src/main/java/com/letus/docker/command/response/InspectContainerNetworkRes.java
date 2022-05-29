package com.letus.docker.command.response;

import com.github.dockerjava.api.model.ContainerNetwork;

import java.util.Map;

public class InspectContainerNetworkRes extends Response<Map<String, ContainerNetwork>> {
    public InspectContainerNetworkRes(Map<String, ContainerNetwork> networkMap) {
        super(networkMap);
    }

    public Map<String, ContainerNetwork> getNetworkMap() {
        return this.value;
    }
}

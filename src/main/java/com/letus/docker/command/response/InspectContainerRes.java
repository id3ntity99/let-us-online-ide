package com.letus.docker.command.response;

import com.github.dockerjava.api.command.InspectContainerResponse;

@Deprecated
public class InspectContainerRes extends Response<InspectContainerResponse> {
    public InspectContainerRes(InspectContainerResponse res) {
        super(res);
    }

    public InspectContainerResponse getInspection() {
        return this.value;
    }
}

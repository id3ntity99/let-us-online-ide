package com.letus.command.response;

import com.github.dockerjava.api.command.InspectContainerResponse;

public class InspectContainerRes extends Response<InspectContainerResponse> {
    public InspectContainerRes(InspectContainerResponse res) {
        super(res);
    }

    public InspectContainerResponse getInspection() {
        return this.value;
    }
}

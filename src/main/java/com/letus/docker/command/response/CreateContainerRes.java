package com.letus.docker.command.response;

import com.github.dockerjava.api.model.Container;

public class CreateContainerRes extends Response<Container> {
    public CreateContainerRes(Container container) {
        super(container);
    }

    public Container getContainer() {
        return this.value;
    }
}

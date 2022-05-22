package com.letus.command.response;

import com.github.dockerjava.api.model.Container;

public class StartContainerRes extends Response<Container> {
    public StartContainerRes(Container container) {
        super(container);
    }

    public Container getContainer() {
        return this.value;
    }
}

package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.ContainerManager;

public class StartUpContainerCmd implements ContainerCommand {
    private String imageName;
    private final ContainerManager containerManager = new ContainerManager();

    public StartUpContainerCmd withImage(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public Container exec() {
        Container container = containerManager.create(imageName);
        //containerManager.createNetwork();
        containerManager.connectToNetwork(container);
        containerManager.start(container);
        return container;
    }
}

package com.letus.command;

import com.github.dockerjava.api.model.Container;
import com.letus.command.response.CreateContainerRes;
import com.letus.docker.ContainerManager;

/**
 * This command is responsible for interacting with ContainerManager.createContainer().
 * If a Invoker
 */
public class CreateContainerCmd implements Command {
    private String imageName;
    private final ContainerManager containerManager = new ContainerManager();

    public CreateContainerCmd withImage(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public CreateContainerRes exec() {
        Container container = containerManager.create(imageName);
        return new CreateContainerRes(container);
    }
}

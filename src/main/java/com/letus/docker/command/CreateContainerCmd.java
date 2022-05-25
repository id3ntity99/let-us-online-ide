package com.letus.docker.command;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.CreateContainerRes;
import com.letus.docker.ContainerManager;

/**
 * This command is responsible for interacting with ContainerManager.createContainer()
 * which creates a new Docker container.
 */
public class CreateContainerCmd extends AbstractCommand{
    private String imageName;
    private ContainerManager manager;

    /**
     *  A method to initialize imageName field of the instance.
     * @param imageName A name of image that will be used to create new Docker container.
     * @return Returns CreateContainerCmd object with initialized imageName field.
     */
    public CreateContainerCmd withImage(String imageName) {
        this.imageName = imageName;
        return this;
    }

    /**
     *  A method to initialize manager field of the instance.
     * @param manager A ContainerManager.
     * @return Returns CreateContainerCmd object with initialized manager field.
     */
    public CreateContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }


    /**
     * An execute method that will be triggered by ContainerCommands client object.
     * Execution of this method will eventually create a new Docker container on the server host.
     * @return Returns a response that contains necessary information for later uses.
     */
    public CreateContainerRes exec() {
        Container container = manager.create(imageName);
        return new CreateContainerRes(container);
    }
}

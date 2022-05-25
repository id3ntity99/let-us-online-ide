package com.letus.docker.command;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.StartContainerRes;
import com.letus.docker.ContainerManager;

/**
 * This command is responsible for starting a pre-created container.
 */
public class StartContainerCmd implements Command{
    private ContainerManager manager;
    private Container container;

    /**
     * A method to initialize container field of the instance.
     * @param container A container object to start.
     * @return Returns StartContainerCmd object with initialized container field.
     */
    public StartContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     *  A method to initialize manager field of the instance.
     * @param manager A ContainerManager.
     * @return Returns CreateContainerCmd object with initialized manager field.
     */
    public StartContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }


    /**
     * Execution of this method will eventually start a pre-created container on the server host.
     * @return Returns a response object that contains necessary information for later uses.
     */
    public StartContainerRes exec() {
        manager.start(container);
        return new StartContainerRes(container);
    }
}

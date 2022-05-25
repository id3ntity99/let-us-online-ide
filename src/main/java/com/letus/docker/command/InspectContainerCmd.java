package com.letus.docker.command;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.InspectContainerRes;
import com.letus.docker.ContainerManager;

/**
 * This command can be used to inspect a container to get some information.
 */
public class InspectContainerCmd implements Command{
    private ContainerManager manager;
    private Container container;

    /**
     * A method to initialize container field of the instance.
     * @param container A container object to inspect.
     * @return Returns InspectContainerCmd object with initialized container field.
     */
    public InspectContainerCmd withContainer(Container container) {
        this.container = container;
        return this;
    }


    /**
     * A method to initialize manager field of the instance.
     * @param manager A ContainerManager.
     * @return Returns InspectContainerCmd object with initialized manager field.
     */
    public InspectContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }


    /**
     * Execution of this method will inspect a pre-created container.
     * @return Returns a response object that contains necessary information for later uses.
     */
    public InspectContainerRes exec() {
        InspectContainerResponse res = manager.inspect(container);
        return new InspectContainerRes(res);
    }
}

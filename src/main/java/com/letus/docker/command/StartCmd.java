package com.letus.docker.command;

import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.StartContainerRes;
import com.letus.docker.ContainerManager;

/**
 * This command is responsible for starting a pre-created container.
 */
public class StartCmd extends AbstractCommand {
    private Container container;

    /**
     * A method to initialize container field of the instance.
     *
     * @param container A container object to start.
     * @return Returns StartContainerCmd object with initialized container field.
     */
    public StartCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     * Execution of this method will eventually start a pre-created container on the server host.
     *
     * @return Returns a response object that contains necessary information for later uses.
     */
    public StartContainerRes exec() {
        StartContainerCmd cmd = dockerClient.startContainerCmd(container.getId());
        cmd.exec();
        return new StartContainerRes(container);
    }
}

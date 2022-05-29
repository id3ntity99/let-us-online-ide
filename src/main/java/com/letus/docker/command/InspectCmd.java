package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;

import javax.annotation.CheckForNull;

/**
 * This command can be used to inspect a container to get some information.
 */
public class InspectCmd extends AbstractCommand<InspectCmd, InspectContainerResponse> {
    @CheckForNull
    private Container container;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A Docker-api client as a receiver.
     * @return Returns Inspect object with initialized dockerClient field.
     */
    @Override
    public InspectCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    /**
     * A method to initialize container field of the instance.
     *
     * @param container A container object to inspect.
     * @return Returns InspectContainerCmd object with initialized container field.
     */
    public InspectCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     * Execution of this method will inspect a pre-created container.
     *
     * @return Returns a response object that contains necessary information for later uses.
     */
    public InspectContainerResponse exec() {
        InspectContainerCmd cmd = dockerClient.inspectContainerCmd(container.getId());
        InspectContainerResponse res = null;
        try {
            res = cmd.exec();
        } catch (NotFoundException e) {
            logger.error("Cannot inspect a container...", e);
        }
        return res;
    }
}

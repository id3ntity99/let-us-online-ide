package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.InspectContainerRes;

/**
 * This command can be used to inspect a container to get some information.
 */
public class InspectCmd extends AbstractCommand<InspectCmd, InspectContainerRes> {
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
    public InspectContainerRes exec() {
        InspectContainerCmd cmd = dockerClient.inspectContainerCmd(container.getId());
        InspectContainerResponse res = cmd.exec();
        return new InspectContainerRes(res);
    }
}

package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;

import javax.annotation.CheckForNull;

/**
 * This command is responsible for starting a pre-created container.
 */
public class StartCmd extends AbstractCommand<StartCmd, Void> {
    @CheckForNull
    private Container container;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A Docker-api client as a receiver.
     * @return Returns StartCmd object with initialized dockerClient field.
     */
    @Override
    public StartCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

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
    public Void exec() throws NotModifiedException, NotFoundException {
        StartContainerCmd cmd = dockerClient.startContainerCmd(container.getId());
        try {
            cmd.exec();
        } catch (NotModifiedException e) {
            throw new NotModifiedException(e.getMessage(), e);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
        return null;
    }
}

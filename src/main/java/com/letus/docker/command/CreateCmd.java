package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.letus.docker.command.response.CreateContainerRes;

import javax.annotation.CheckForNull;

/**
 * This command is responsible for interacting with ContainerManager.createContainer()
 * which creates a new Docker container.
 */
public class CreateCmd extends AbstractCommand<CreateCmd, CreateContainerRes> {
    @CheckForNull
    private String imageName;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A Docker-api client as a receiver.
     * @return Returns CreateCmd object with initialized dockerClient field.
     */
    @Override
    public CreateCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    /**
     * A method to initialize imageName field of the instance.
     *
     * @param imageName A name of image that will be used to create new Docker container.
     * @return Returns CreateCmd object with initialized imageName field.
     */
    public CreateCmd withImage(String imageName) {
        this.imageName = imageName;
        return this;
    }

    /**
     * An execute method that will be triggered by ContainerCommands client object.
     * Execution of this method will eventually create a new Docker container on the server host.
     *
     * @return Returns a response that contains necessary information for later uses.
     */
    public CreateContainerRes exec() {
        CreateContainerRes res = null;
        try (CreateContainerCmd cmd = dockerClient.createContainerCmd(imageName)) {
            try {
                String containerId = cmd.withAttachStderr(true)
                        .withAttachStdin(true)
                        .withAttachStderr(true)
                        .withTty(true)
                        .exec()
                        .getId();
                res = new CreateContainerRes(search(containerId));
            } catch (ConflictException | NotFoundException e) {
                logger.error("Cannot create new docker container...", e);
            }
            return res;
        }
    }
}

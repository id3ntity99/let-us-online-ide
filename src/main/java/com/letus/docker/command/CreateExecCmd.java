package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.CreateExecContainerRes;

import javax.annotation.CheckForNull;

/**
 * This class is responsible for creating 'docker exec' channel to the specified container.
 */
public class CreateExecCmd extends AbstractCommand<CreateExecCmd, CreateExecContainerRes> {
    @CheckForNull
    Container container;

    /**
     * A method to initialize dockerClient field of the instance.
     *
     * @param dockerClient A docker-api client as a parameter.
     * @return Returns CreateExecCmd object with the initialized dockerClient field.
     */
    @Override
    public CreateExecCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }


    /**
     * A method to initialize container field of the instance.
     *
     * @param container A container object to exec-connect.
     * @return Returns an object with the initialized dockerClient field.
     */
    public CreateExecCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     * Invocation of this method will connect to the container for interaction.
     *
     * @return A Response object that contains necessary information for later uses.
     * Particularly, CreateExecContainerRes has field execId that is used to start exec.
     */
    public CreateExecContainerRes exec() {
        ExecCreateCmd createCmd = dockerClient.execCreateCmd(container.getId());
        ExecCreateCmdResponse res = createCmd.withCmd("/bin/sh")
                .withTty(true)
                .withAttachStdin(true)
                .withUser("root")
                .withAttachStdout(true)
                .exec();
        return new CreateExecContainerRes(res.getId());
    }
}

package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.CreateExecContainerRes;


public class CreateExecCmd extends AbstractCommand<CreateExecCmd, CreateExecContainerRes> {
    Container container;

    public CreateExecCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    public CreateExecCmd withContainer(Container container) {
        this.container = container;
        return this;
    }

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

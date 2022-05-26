package com.letus.docker.command;

import com.github.dockerjava.api.command.ExecCreateCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.response.CreateExecContainerRes;


public class CreateExecCmd extends AbstractCommand{
    Container container;
    String execId;

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

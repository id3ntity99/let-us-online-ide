package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecStartCmd;
import com.letus.docker.ExecStartResultCallback;
import com.letus.user.User;

import javax.annotation.Nullable;

public class StartExecCmd extends AbstractCommand<StartExecCmd, Void> implements Runnable {
    String execId;
    User user;

    public StartExecCmd withDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    public StartExecCmd withExecId(String execId) {
        this.execId = execId;
        return this;
    }

    public StartExecCmd withUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public void run() {
        ExecStartCmd cmd = dockerClient.execStartCmd(execId);
        ExecStartResultCallback callback = new ExecStartResultCallback(user);
        try {
            cmd.withDetach(false)
                    .withStdIn(user.getInputStream())
                    .withTty(true)
                    .exec(callback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Void exec() {
        Thread execThread = new Thread(this);
        execThread.setName("execStartThread");
        execThread.start();
        return null;
    }
}

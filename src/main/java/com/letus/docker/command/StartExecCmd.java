package com.letus.docker.command;

import com.github.dockerjava.api.command.ExecStartCmd;
import com.letus.docker.ExecStartResultCallback;
import com.letus.docker.command.response.DefaultResponse;
import com.letus.user.User;

public class StartExecCmd extends AbstractCommand implements Runnable {
    String execId;
    User user;

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

    public DefaultResponse exec() {
        Thread execThread = new Thread(this);
        execThread.setName("execStartThread");
        execThread.start();
        if (Thread.currentThread().isAlive()) {
            return new DefaultResponse(true);
        } else {
            return new DefaultResponse(false);
        }
    }
}

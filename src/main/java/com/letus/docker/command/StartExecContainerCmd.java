package com.letus.docker.command;

import com.letus.docker.ContainerManager;
import com.letus.user.User;

public class StartExecContainerCmd implements Runnable {
    ContainerManager manager;
    String execId;
    User user;

    public StartExecContainerCmd withExecId(String execId) {
        this.execId = execId;
        return this;
    }

    public StartExecContainerCmd withUser(User user) {
        this.user = user;
        return this;
    }

    public StartExecContainerCmd withManager(ContainerManager manager) {
        this.manager = manager;
        return this;
    }

    @Override
    public void run() {
        manager.startExec(execId, user);
    }

    public void exec() {
        Thread execThread = new Thread(this);
        execThread.setName("execStartThread");
        execThread.start();
    }
}

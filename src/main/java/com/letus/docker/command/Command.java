package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;

/**
 * Command to interact with ContainerManager.
 * Every commands that implement this interface must return
 * concrete Response objects that describe result of execution of ContainerManager's method.
 */
public interface Command<COMMAND, EXEC_RESULT> {
    EXEC_RESULT exec();
    COMMAND withDockerClient(DockerClient dockerClient);
}

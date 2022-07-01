package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;

/**
 * Command to interact with ContainerManager.
 * Every commands that implement this interface must return
 * concrete Response objects that describe result of execution of ContainerManager's method.
 */
public interface Command<COMMAND, EXEC_RESULT> {
    EXEC_RESULT exec() throws DockerException;
    COMMAND withDockerClient(DockerClient dockerClient);
}

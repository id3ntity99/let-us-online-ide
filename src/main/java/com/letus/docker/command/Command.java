package com.letus.docker.command;

import com.letus.docker.command.response.Response;

/**
 * Command to interact with ContainerManager.
 * Every commands that implement this interface must return
 * concrete Response objects that describe result of execution of ContainerManager's method.
 */
public interface Command {
    Response exec();
}

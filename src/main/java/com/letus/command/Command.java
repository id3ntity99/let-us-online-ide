package com.letus.command;

import com.letus.command.response.Response;

/**
 * Command to interact with ContainerManager.
 * Every commands that implement this interface must return
 * concrete Response objects that describe result of execution of ContainerManager's method.
 */
public interface Command {
    /*TODO
       Every command must have ContainerManager field and  withManager(ContainerManager manager)
       method to satisfy the condition of Invoker of Command Pattern. */
    Response exec();
}

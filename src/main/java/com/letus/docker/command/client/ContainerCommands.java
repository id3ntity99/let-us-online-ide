package com.letus.docker.command.client;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.*;
import com.letus.docker.command.response.DefaultResponse;
import com.letus.docker.command.response.InspectContainerNetworkRes;
import com.letus.docker.command.response.InspectContainerRes;
import com.letus.user.User;

/**
 * An instance of this class plays the role of both an Invoker(, which is part of Command pattern)
 * and a Facade. Technically, this class is an Invoker as well as a Client because
 * it creates Command objects and triggers exec method of the commands.
 * The real Client (the Websocket class) just only need to use this facade object
 * to interact with ContainerManager (the receiver) indirectly.
 * After that, this class returns corresponding concrete Response objects for later uses.
 */
public class ContainerCommands {
    private ContainerCommands() {
    }

    public static Container create(String image) {
        return new CreateCmd().withImage(image)
                .exec()
                .getContainer();
    }

    public static Container start(Container container) {
        return new StartCmd().withContainer(container)
                .exec()
                .getContainer();
    }

    public static DefaultResponse stop(Container container) {
        return new StopCmd().withContainer(container).exec();
    }


    public static InspectContainerNetworkRes inspectNetwork(Container container) {
        return new InspectNetworkCmd().withContainer(container)
                .exec();
    }

    public static InspectContainerRes inspect(Container container) {
        return new InspectCmd().withContainer(container).exec();
    }

    public static String createExec(Container container) {
        return new CreateExecCmd().withContainer(container)
                .exec()
                .getExecId();
    }

    public static void startExec(User user, String execId) {
        new StartExecCmd().withExecId(execId)
                .withUser(user)
                .exec();
    }

    public static boolean remove(Container container) {
        return new RemoveCmd().withContainer(container).exec().getSuccess();
    }
}

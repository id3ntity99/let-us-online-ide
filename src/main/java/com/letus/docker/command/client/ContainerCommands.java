package com.letus.docker.command.client;

import com.github.dockerjava.api.model.Container;
import com.letus.docker.command.*;
import com.letus.docker.command.response.InspectContainerNetworkRes;
import com.letus.docker.command.response.InspectContainerRes;
import com.letus.docker.ContainerManager;
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
    private static final ContainerManager MANAGER = new ContainerManager();

    private ContainerCommands() {
    }

    public static Container create(String image) {
        return new CreateContainerCmd().withImage(image)
                .withManager(MANAGER)
                .exec()
                .getContainer();
    }

    public static Container start(Container container) {
        return new StartContainerCmd().withContainer(container)
                .withManager(MANAGER)
                .exec()
                .getContainer();
    }


    public static InspectContainerNetworkRes inspectNetwork(Container container) {
        return new InspectContainerNetworkCmd().withContainer(container)
                .withManager(MANAGER)
                .exec();
    }

    public static InspectContainerRes inspect(Container container) {
        return new InspectContainerCmd().withContainer(container)
                .withManager(MANAGER)
                .exec();
    }

    public static String createExec(Container container) {
        return new CreateExecContainerCmd().withContainer(container)
                .withManager(MANAGER)
                .exec()
                .getExecId();
    }

    public static void startExec(User user, String execId) {
        new StartExecContainerCmd().withExecId(execId)
                .withManager(MANAGER)
                .withUser(user)
                .exec();
    }
}

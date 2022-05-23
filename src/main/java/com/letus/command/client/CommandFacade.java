package com.letus.command.client;

import com.github.dockerjava.api.model.Container;
import com.letus.command.*;
import com.letus.command.response.InspectContainerNetworkRes;
import com.letus.command.response.InspectContainerRes;
import com.letus.user.User;


public class CommandFacade {
    private CommandFacade() {
    }

    public static Container createContainerCmd(String image) {
        return new CreateContainerCmd().withImage(image)
                .exec()
                .getContainer();
    }
    public static Container startContainerCmd(Container container) {
        return new StartContainerCmd().withContainer(container)
                .exec()
                .getContainer();
    }


    public static InspectContainerNetworkRes inspectContainerNetworkCmd(Container container) {
        return new InspectContainerNetworkCmd().withContainer(container).exec();
    }

    public static InspectContainerRes inspectContainer(Container container) {
        return new InspectContainerCmd().withContainer(container).exec();
    }

    public static String createExecContainerCmd(Container container) {
        return new CreateExecContainerCmd().withContainer(container)
                .exec()
                .getExecId();
    }

    public static void startExecContainerCmd(User user, String execId) {
        new StartExecContainerCmd().withExecId(execId)
                .withUser(user)
                .exec();
    }
}

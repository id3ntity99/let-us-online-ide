package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An abstract class that implements Command and is extended by all concrete commands.
 *
 * @param <COMMAND>     This parameter stands for command as a return type.
 * @param <EXEC_RESULT> This parameter stands for response of exec().
 */
public abstract class AbstractCommand<COMMAND, EXEC_RESULT> implements Command<COMMAND, EXEC_RESULT> {
    @CheckForNull
    protected DockerClient dockerClient;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * This method is used for searching container.
     *
     * @param containerId A container id to search.
     * @return Returns a container or null if there is no search result.
     */
    protected Container search(String containerId) {
        ListContainersCmd cmd = dockerClient.listContainersCmd();
        Collection<String> containerIdArrayList = new ArrayList<>();
        containerIdArrayList.add(containerId);
        cmd.withShowAll(true).withIdFilter(containerIdArrayList);
        List<Container> containerList = cmd.exec();
        if (containerList.isEmpty()) {
            return null;
        }
        return containerList.get(0);
    }
}

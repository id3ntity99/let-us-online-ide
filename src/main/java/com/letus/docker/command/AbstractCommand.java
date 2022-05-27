package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCommand<COMMAND, EXEC_RESULT> implements Command<COMMAND, EXEC_RESULT> {
    /*TODO
       모든 Command에 javadoc 주석달기
       모든 exec() 예외처리
       모든 fields에 @checkfornull 달기*/
    protected DockerClient dockerClient;

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

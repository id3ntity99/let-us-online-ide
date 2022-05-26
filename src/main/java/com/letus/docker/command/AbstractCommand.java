package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCommand implements Command {
    protected final DockerClientConfig config = new DefaultDockerClientConfig.Builder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
    protected final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
    protected final DockerClient dockerClient = DockerClientBuilder.getInstance()
            .withDockerHttpClient(httpClient)
            .build();

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

package com.letus.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.letus.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContainerManager {
    private final DockerClientConfig config = new DefaultDockerClientConfig.Builder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
    private final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();

    private final DockerClient client = DockerClientBuilder.getInstance()
            .withDockerHttpClient(httpClient)
            .build();

    private Container search(String containerId) {
        try (ListContainersCmd cmd = client.listContainersCmd()) {
            Collection<String> containerIdArrayList = new ArrayList<>();
            containerIdArrayList.add(containerId);
            cmd.withShowAll(true).withIdFilter(containerIdArrayList);
            List<Container> containerList = cmd.exec();
            return containerList.get(0);
        }
    }

    public Container create(String image) {
        try (CreateContainerCmd createCmd = client.createContainerCmd(image)) {
            CreateContainerResponse res = createCmd.withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withTty(true)
                    .exec();
            String containerId = res.getId();
            return search(containerId);
        }
    }

    public void start(Container container) {
        try (StartContainerCmd startCmd = client.startContainerCmd(container.getId())) {
            startCmd.exec();
        }
    }

    public InspectContainerResponse inspect(Container container) {
        try (InspectContainerCmd cmd = client.inspectContainerCmd(container.getId())) {
            return cmd.exec();
        }
    }

    public String createExec(Container container, User user) {
        ExecCreateCmd createCmd = client.execCreateCmd(container.getId());
        ExecCreateCmdResponse res = createCmd.withCmd("/bin/sh")
                .withTty(true)
                .withAttachStdin(true)
                .withUser("root")
                .withAttachStdout(true)
                .exec();
        return res.getId();
    }

    public void startExec(String execId, User user) {
        ExecStartCmd startCmd = client.execStartCmd(execId);
        ExecStartResultCallback callback = new ExecStartResultCallback(user);
        try {
            startCmd.withDetach(false)
                    .withStdIn(user.getInputStream())
                    .withTty(true)
                    .exec(callback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stop(Container container) {
        try (StopContainerCmd cmd = client.stopContainerCmd(container.getId())) {
            cmd.exec();
        }
    }

    public void destroy(Container container) {
        client.killContainerCmd(container.getId()).exec();
    }
}

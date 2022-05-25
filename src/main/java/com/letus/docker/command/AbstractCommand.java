package com.letus.docker.command;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.letus.docker.command.response.Response;

public abstract class AbstractCommand implements Command{
    protected final DockerClientConfig config = new DefaultDockerClientConfig.Builder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
    protected final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
    protected final DockerClient dockerClient = DockerClientBuilder.getInstance()
            .withDockerHttpClient(httpClient)
            .build();

    abstract public Response exec();
}

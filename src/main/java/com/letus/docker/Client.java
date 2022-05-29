package com.letus.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

public enum Client {
    DOCKER_CLIENT(DockerClientBuilder.getInstance());

    private final DefaultDockerClientConfig config = new DefaultDockerClientConfig.Builder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
    private final DockerHttpClient httpClient = new JerseyDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
    private final DockerClient dockerClient;

    public DockerClient getDockerClient() {
        return dockerClient;
    }

    Client(DockerClientBuilder builder) {
        this.dockerClient = builder.withDockerHttpClient(httpClient).build();
    }
}

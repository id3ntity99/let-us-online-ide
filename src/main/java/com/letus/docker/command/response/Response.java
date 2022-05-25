package com.letus.docker.command.response;

public abstract class Response<T> {
    T value;

    public Response(T value) {
        this.value = value;
    }
}

package com.letus.docker.command.response;

/**
 * This class contains the necessary information about execution of commands.
 * @param <T> Type of the field.
 */
public abstract class Response<T> {
    T value;

    protected Response(T value) {
        this.value = value;
    }
}

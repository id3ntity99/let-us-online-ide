package com.letus.ssh.exceptions;


/*
* @deprecated Use Docker-exec command instead of SSH connection.
* */
@Deprecated
public class InputTooLargeException extends Exception{
    public InputTooLargeException(String message) {
        super(message);
    }
}

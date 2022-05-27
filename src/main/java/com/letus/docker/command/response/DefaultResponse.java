package com.letus.docker.command.response;

@Deprecated
public class DefaultResponse extends Response<Boolean>{
    public DefaultResponse(boolean isSuccess) {
        super(isSuccess);
    }

    public boolean getSuccess() {
        return this.value;
    }
}

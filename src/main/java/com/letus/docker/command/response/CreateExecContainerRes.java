package com.letus.docker.command.response;

@Deprecated
public class CreateExecContainerRes extends Response<String>{
    public CreateExecContainerRes(String execId) {
        super(execId);
    }

    public String getExecId() {
        return this.value;
    }
}

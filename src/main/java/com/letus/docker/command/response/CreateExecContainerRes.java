package com.letus.command.response;

public class CreateExecContainerRes extends Response<String>{
    public CreateExecContainerRes(String execId) {
        super(execId);
    }

    public String getExecId() {
        return this.value;
    }
}

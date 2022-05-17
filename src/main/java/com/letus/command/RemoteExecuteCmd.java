package com.letus.command;

import com.jcraft.jsch.ChannelShell;
import com.letus.ssh.SSHExecution;
import com.letus.ssh.exceptions.InputTooLargeException;
import com.letus.user.User;

import javax.websocket.Session;
import java.io.IOException;

public class RemoteExecuteCmd implements Command {
    private byte[] cmd;

    private User user;

    public RemoteExecuteCmd withByteCmd(byte[] cmd) {
        this.cmd = cmd;
        return this;
    }

    public RemoteExecuteCmd withUserObject(User user) {
        this.user = user;
        return this;
    }


    public void exec() {
        try {
            SSHExecution.readExecResult(cmd, user);
        } catch (IOException | InputTooLargeException e) {
            e.printStackTrace();
        }
    }
}

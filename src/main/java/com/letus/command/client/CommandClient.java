package com.letus.command.client;

import com.letus.command.RemoteExecuteCmd;
import com.letus.command.StartUpContainerCmd;

public class CommandClient {
    private CommandClient() {
    }

    public static RemoteExecuteCmd remoteExecuteCmd() {
        return new RemoteExecuteCmd();
    }

    public static StartUpContainerCmd startUpContainerCmd() {
        return new StartUpContainerCmd();
    }
}

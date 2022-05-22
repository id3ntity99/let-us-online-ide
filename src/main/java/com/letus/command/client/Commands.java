package com.letus.command.client;

import com.letus.command.*;

public class Commands {
    private Commands() {
    }

    public static CreateContainerCmd createContainerCmd() {
        return new CreateContainerCmd();
    }
    public static StartContainerCmd startContainerCmd() {return new StartContainerCmd();}

    public static InspectContainerNetworkCmd inspectContainerNetworkCmd() {
        return new InspectContainerNetworkCmd();
    }

    public static InspectContainerCmd inspectContainer() {return new InspectContainerCmd();}

    public static CreateExecContainerCmd createExecContainerCmd() {return new CreateExecContainerCmd();}

    public static StartExecContainerCmd startExecContainerCmd() {return new StartExecContainerCmd();}
}

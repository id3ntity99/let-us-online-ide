package com.letus.command;

import com.github.dockerjava.api.model.Container;

public interface ContainerCommand {
    Container exec();
}

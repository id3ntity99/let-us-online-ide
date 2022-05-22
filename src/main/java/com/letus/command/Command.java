package com.letus.command;

import com.letus.command.response.Response;

public interface Command {
    Response exec();
}

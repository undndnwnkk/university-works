package ru.lab06.command;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private final String commandName;
    private final Object[] arguments;

    public CommandRequest(String commandName, Object[] arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
package ru.lab06.command;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private final String commandName;
    private final Object[] arguments;

    private final String login;
    private final String password;

    public CommandRequest(String commandName, Object[] arguments, String login, String password) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }



    public String getCommandName() {
        return commandName;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
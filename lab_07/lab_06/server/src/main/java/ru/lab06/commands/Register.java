package ru.lab06.commands;

import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.storage.StorageLike;

public class Register implements Command {
    private final Object[] args;

    public Register(Object[] args) {
        this.args = args;
    }

    @Override
    public CommandResponse execute(StorageLike storage, String loginNew) {
        if (args.length < 2) {
            return new CommandResponse("Not enough arguments");
        }

        String login = (String) args[0];
        String password = (String) args[1];

        boolean success = UserManager.register(login, password);

        if(success) {
            return new CommandResponse("Registration was successful");
        } else {
            return new CommandResponse("Registration failed");
        }
    }
}

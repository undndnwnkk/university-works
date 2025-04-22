package ru.lab06.network;

import ru.lab06.command.Command;
import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.commands.Help;
import ru.lab06.commands.Info;
import ru.lab06.core.Storage;

public class CommandExecutor {
    public static CommandResponse execute(CommandRequest request, Storage storage) {
        String commandName = request.getCommandName();
        Object[] arguments = request.getArguments();

        Command command = switch (commandName) {
            case "info" -> new Info();
            case "help" -> new Help();
            // TODO
            default -> null;
        };

        if (command == null) {
            return new CommandResponse("Unknown command: " + commandName);
        }

        return command.execute(storage);
    }
}

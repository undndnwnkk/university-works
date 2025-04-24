package ru.lab06.network;

import ru.lab06.command.Command;
import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.commands.*;
import ru.lab06.core.Storage;

public class CommandExecutor {

    public static CommandResponse execute(CommandRequest request, Storage storage) {
        String name = request.getCommandName();
        Object[] args = request.getArguments();

        Command command = switch (name) {
            case "add" -> new Add(args);
            case "info" -> new Info();
            case "show" -> new Show();
            case "help" -> new Help();
            case "remove_by_id" -> new RemoveById(args);
            case "update" -> new Update(args);
            case "remove_first" -> new RemoveFirst();
            case "filter_by_transport" -> new FilterByTransport(args);
            case "filter_contains_name" -> new FilterContainsName(args);
            case "min_by_id" -> new MinById();
            case "sort" -> new Sort();
            case "clear" -> new Clear();
            case "execute_script" -> new ExecuteScript(args);
            default -> null;
        };

        if (command == null) {
            return new CommandResponse("Неизвестная команда: " + name);
        }

        return command.execute(storage);
    }


    public static Command createCommand(String commandName, Object[] args) {
        return switch (commandName.toLowerCase()) {
            case "add" -> new Add(args);
            case "update" -> new Update(args);
            case "remove_by_id" -> new RemoveById(args);
            case "remove_first" -> new RemoveFirst();
            case "filter_contains_name" -> new FilterContainsName(args);
            case "filter_by_transport" -> new FilterByTransport(args);
            case "info" -> new Info();
            case "show" -> new Show();
            case "clear" -> new Clear();
            case "min_by_id" -> new MinById();
            case "reorder" -> new Reorder();
            case "sort" -> new Sort();
            case "help" -> new Help();
            case "execute_script" -> new ExecuteScript(args);
            default -> null;
        };
    }
}

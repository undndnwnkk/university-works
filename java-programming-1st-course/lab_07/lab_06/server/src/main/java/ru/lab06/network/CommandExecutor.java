package ru.lab06.network;

import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandRequest;
import ru.lab06.command.CommandResponse;
import ru.lab06.commands.*;
import ru.lab06.core.Storage;

public class CommandExecutor {

    public static CommandResponse execute(CommandRequest request, Storage storage) {
        String login = request.getLogin();
        String password = request.getPassword();
        String name = request.getCommandName();
        Object[] args = request.getArguments();

        if (!name.equals("register") && !UserManager.login(login, password)) {
            return new CommandResponse("Authorization failed: wrong login or password.");
        }

        System.out.println("Authenticated user: " + login);

        Command command = switch (name) {
            case "add" -> new Add(args);
            case "update" -> new Update(args);
            case "remove_by_id" -> new RemoveById(args);
            case "filter_contains_name" -> new FilterContainsName(args);
            case "filter_by_transport" -> new FilterByTransport(args);
            case "execute_script" -> new ExecuteScript(args);
            case "info" -> new Info();
            case "show" -> new Show();
            case "clear" -> new Clear();
            case "min_by_id" -> new MinById();
            case "remove_first" -> new RemoveFirst();
            case "reorder" -> new Reorder();
            case "sort" -> new Sort();
            case "help" -> new Help();
            case "register" -> new Register(args);
            default -> null;
        };

        if (command == null) {
            return new CommandResponse("Unknown command: " + name);
        }

        return command.execute(storage, login);
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

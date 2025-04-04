package ru.lab05.network;

import ru.lab05.commands.CommandRequest;
import ru.lab05.commands.CommandResponse;
import ru.lab05.commands.*;

import ru.lab05.core.Storage;
import ru.lab05.model.Flat;

public class CommandExecutor {

    private final Storage storage;
    private final String filename;

    public CommandExecutor(Storage storage, String filename) {
        this.storage = storage;
        this.filename = filename;
    }

    public CommandResponse execute(CommandRequest request) {
        String command = request.getCommandName();
        Object arg = request.getArgument();

        return switch (command) {
            case "help" -> new CommandResponse(HelpCommand.execute());
            case "info" -> new CommandResponse(InfoCommand.execute(storage));
            case "show" -> {
                ShowCommand show = new ShowCommand(storage);
                String output = show.execute();
                yield new CommandResponse(output);
            }
            case "add" -> {
                Flat flat = (Flat) request.getArgument();
                new AddCommand(storage, flat);
                yield new CommandResponse("Add command executed");
            }
            case "update" -> {
                new UpdateCommand(storage, (String[]) arg);
                yield new CommandResponse("Update command executed");
            }
            case "remove_by_id" -> {
                new RemoveByIdCommand(storage, (String[]) arg);
                yield new CommandResponse("Remove by ID command executed");
            }
            case "clear" -> {
                new ClearCommand(storage);
                yield new CommandResponse("Clear command executed");
            }
            case "remove_first" -> {
                new RemoveFirstCommand(storage);
                yield new CommandResponse("Remove first command executed");
            }
            case "reorder" -> {
                new ReorderCommand(storage);
                yield new CommandResponse("Reorder command executed");
            }
            case "sort" -> {
                new SortCommand(storage);
                yield new CommandResponse("Sort command executed");
            }
            case "min_by_id" -> {
                new MinByIdCommand(storage);
                yield new CommandResponse("Min by ID command executed");
            }
            case "filter_by_transport" -> {
                new FilterByTransportCommand(storage, (String[]) arg);
                yield new CommandResponse("Filter by transport command executed");
            }
            case "filter_contains_name" -> {
                new FilterContainsNameCommand(storage, (String[]) arg);
                yield new CommandResponse("Filter contains name command executed");
            }
            default -> new CommandResponse("Unknown command: " + command);
        };
    }
}

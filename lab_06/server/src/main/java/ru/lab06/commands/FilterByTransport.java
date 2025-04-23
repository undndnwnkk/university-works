package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.model.Flat;

/**
 * The FilterByTransport class filters flats by transport type.
 */
public class FilterByTransport implements Command {
    /**
     * Displays flats that match the specified transport type.
     * @param storage the storage containing flats
     * @param commandArguments the command arguments containing transport type
     */
    private String[] commandArguments;

    public FilterByTransport(String[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty");
        } else {
            StringBuilder output = new StringBuilder("Elements with transport type " + commandArguments[0] + ":\n");
            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getTransport() != null &&
                        flat.getTransport().toString().equals(commandArguments[0].toUpperCase())) {
                    output.append(flat.toString() + "\n");
                }
            }

            return new CommandResponse(output.toString());
        }
    }
}

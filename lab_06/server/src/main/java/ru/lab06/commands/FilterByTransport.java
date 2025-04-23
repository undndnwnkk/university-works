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
    private Object[] commandArguments;

    public FilterByTransport(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty");
        } else {
            StringBuilder output = new StringBuilder("Elements with transport type " + commandArguments[0] + ":\n");
            String currentTransport = (String) commandArguments[0];
            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getTransport() != null &&
                        flat.getTransport().toString().equals(currentTransport.toLowerCase())) {
                    output.append(flat + "\n");
                }
            }

            return new CommandResponse(output.toString());
        }
    }
}

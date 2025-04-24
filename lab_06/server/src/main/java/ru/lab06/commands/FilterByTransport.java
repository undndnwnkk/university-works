package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.model.Flat;
import ru.lab06.model.Transport;
import ru.lab06.storage.StorageLike;

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
    public CommandResponse execute(StorageLike storage) {
        if (commandArguments.length < 1) return new CommandResponse("Specify a transport");

        Transport transport;
        try {
            transport = Transport.valueOf(commandArguments[0].toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResponse("Invalid transport value");
        }

        String result = storage.getFlatStorage().stream()
                .filter(flat -> transport.equals(flat.getTransport()))
                .map(Flat::toString)
                .reduce("", (a, b) -> a + b + "\n");

        if (result.isEmpty()) return new CommandResponse("No flats with specified transport");

        return new CommandResponse(result.trim());
    }

}

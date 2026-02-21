
package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

/**
 * The FilterContainsName class filters flats by a substring in their name.
 */
public class FilterContainsName implements Command {
    /**
     * Displays flats whose names contain the given substring.
     * @param storage the storage containing flats
     * @param commandArguments the command arguments containing the substring
     */
    private Object[] commandArguments;

    public FilterContainsName(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        if (commandArguments.length < 1) return new CommandResponse("You must provide a name substring");

        String substring = commandArguments[0].toString().toLowerCase();

        String result = storage.getFlatStorage().stream()
                .filter(flat -> flat.getName().toLowerCase().contains(substring))
                .map(Flat::toString)
                .reduce("", (a, b) -> a + b + "\n");

        if (result.isEmpty()) return new CommandResponse("No matching flats found");

        return new CommandResponse(result.trim());
    }

}

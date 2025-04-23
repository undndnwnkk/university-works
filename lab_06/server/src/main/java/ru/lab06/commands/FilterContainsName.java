
package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;

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
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty");
        } else {
            StringBuilder output = new StringBuilder("Storage contains flats with name " + commandArguments[0] + ":\n");
            String flatName = (String) commandArguments[0];
            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getName().toLowerCase().contains(flatName.toLowerCase())) {
                    output.append(flat.toString()).append("\n");
                }
            }

            return new CommandResponse(output.toString());
        }
    }
}

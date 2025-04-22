package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;

/**
 * The Info class displays information about the current collection.
 */
public class Info implements Command {
    /**
     * Prints information about the collection.
     * @param storage the storage object containing flats
     */
    @Override
    public CommandResponse execute(Storage storage) {
        String output = "Type of collection: " + storage.getClass().getSimpleName()
                + "\nDate of initialization: " + storage.getInitializationDate()
                + "\nCount of collections: " + storage.getFlatStorage().size();

        return new CommandResponse(output);
    }
}

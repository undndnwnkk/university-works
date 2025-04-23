package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
/**
 * The Clear class removes all elements from the storage.
 */
public class Clear implements Command {
    /**
     * Clears all flats from the storage.
     * @param storage the storage to be cleared
     */

    @Override
    public CommandResponse execute(Storage storage) {
        storage.getFlatStorage().clear();

        return new CommandResponse("Storage cleared :)");
    }
}

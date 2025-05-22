package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

/**
 * The RemoveFirst class removes the first element from the storage.
 */
public class RemoveFirst implements Command {
    /**
     * Removes the first flat in the storage.
     * @param storage the storage object containing flats
     */
    @Override
    public CommandResponse execute(StorageLike storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Nothing to remove!");
        } else {
            storage.getFlatStorage().remove(0);
            return new CommandResponse("First element removed successfully!");
        }
    }
}

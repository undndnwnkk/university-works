package ru.lab05.commands;

import ru.lab05.core.Storage;

/**
 * The RemoveFirst class removes the first element from the storage.
 */
public class RemoveFirstCommand {
    /**
     * Removes the first flat in the storage.
     * @param storage the storage object containing flats
     */
    public RemoveFirstCommand(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Nothing to remove!");
        } else {
            storage.getFlatStorage().remove(0);
            System.out.println("First element removed successfully!");
        }
    }
}

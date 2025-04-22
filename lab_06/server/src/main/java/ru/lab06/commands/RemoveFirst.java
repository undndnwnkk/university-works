package ru.lab06.commands;

import ru.lab06.core.Storage;

/**
 * The RemoveFirst class removes the first element from the storage.
 */
public class RemoveFirst {
    /**
     * Removes the first flat in the storage.
     * @param storage the storage object containing flats
     */
    public RemoveFirst(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Nothing to remove!");
        } else {
            storage.getFlatStorage().remove(0);
            System.out.println("First element removed successfully!");
        }
    }
}

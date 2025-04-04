package ru.lab05.commands;

import ru.lab05.core.Storage;
/**
 * The Clear class removes all elements from the storage.
 */
public class ClearCommand {
    /**
     * Clears all flats from the storage.
     * @param storage the storage to be cleared
     */
    public ClearCommand(Storage storage) {
        storage.getFlatStorage().clear();
        System.out.println("Storage cleared!");
    }
}

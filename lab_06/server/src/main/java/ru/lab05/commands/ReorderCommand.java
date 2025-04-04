package ru.lab05.commands;

import ru.lab05.core.Storage;

import java.util.Collections;

/**
 * The Reorder class reverses the order of elements in the storage.
 */
public class ReorderCommand {
    /**
     * Reverses the order of flats in the storage.
     * @param storage the storage object containing flats
     */
    public ReorderCommand(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty, nothing to reverse :(");
        } else {
            Collections.reverse(storage.getFlatStorage());
            System.out.println("Collection reordered successfully!");
        }
    }
}

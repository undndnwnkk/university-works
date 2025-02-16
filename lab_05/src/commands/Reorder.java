package commands;

import storage.Storage;

import java.util.Collections;

/**
 * The Reorder class reverses the order of elements in the storage.
 */
public class Reorder {
    /**
     * Reverses the order of flats in the storage.
     * @param storage the storage object containing flats
     */
    public Reorder(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty, nothing to reverse :(");
        } else {
            Collections.reverse(storage.getFlatStorage());
            System.out.println("Collection reordered successfully!");
        }
    }
}

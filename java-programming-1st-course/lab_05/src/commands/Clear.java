package commands;

import storage.Storage;
/**
 * The Clear class removes all elements from the storage.
 */
public class Clear {
    /**
     * Clears all flats from the storage.
     * @param storage the storage to be cleared
     */
    public Clear(Storage storage) {
        storage.getFlatStorage().clear();
        System.out.println("Storage cleared!");
    }
}

package commands;

import storage.Storage;

/**
 * The Info class displays information about the current collection.
 */
public class Info {
    /**
     * Prints information about the collection.
     * @param storage the storage object containing flats
     */
    public Info(Storage storage) {
        System.out.println("Collection information:");
        System.out.println("Type of collection: " + storage.getClass().getSimpleName());
        System.out.println("Initialization date: " + storage.getInitializationDate());
        System.out.println("Number of elements: " + storage.getFlatStorage().size());
    }
}

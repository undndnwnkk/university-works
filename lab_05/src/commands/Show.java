package commands;

import collections.Flat;
import storage.Storage;

import java.util.Vector;

/**
 * The Show class displays all elements in the collection.
 */
public class Show {
    /**
     * Prints all flats stored in the collection.
     * @param storage the storage object containing flats
     */
    public Show(Storage storage) {
        Vector<Flat> storageElements = storage.getFlatStorage();

        if (storageElements.isEmpty()) {
            System.out.println("No elements in storage.");
        } else {
            for (Flat element : storageElements) {
                System.out.println(element);
            }
        }
    }
}

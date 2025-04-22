package ru.lab06.commands;

import ru.lab06.model.Flat;
import ru.lab06.core.Storage;

import java.util.Collections;
import java.util.Vector;

/**
 * The MinById class finds and prints the flat with the smallest ID.
 */
public class MinById {
    /**
     * Prints the flat with the smallest ID.
     * @param storage the storage object containing flats
     */
    public MinById(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
            return;
        }

        Vector<Flat> storageElementsCopy = new Vector<>(storage.getFlatStorage());
        Collections.sort(storageElementsCopy, (flat1, flat2) -> Integer.compare(flat1.getId(), flat2.getId()));
        System.out.println("Element with smallest ID: " + storageElementsCopy.get(0));
    }
}

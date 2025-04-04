
package ru.lab05.commands;

import ru.lab05.model.Flat;
import ru.lab05.core.Storage;

/**
 * The FilterContainsName class filters flats by a substring in their name.
 */
public class FilterContainsNameCommand {
    /**
     * Displays flats whose names contain the given substring.
     * @param storage the storage containing flats
     * @param commandArguments the command arguments containing the substring
     */
    public FilterContainsNameCommand(Storage storage, String[] commandArguments) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
        } else {
            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getName().toLowerCase().contains(commandArguments[0].toLowerCase())) {
                    System.out.println(flat);
                }
            }
        }
    }
}

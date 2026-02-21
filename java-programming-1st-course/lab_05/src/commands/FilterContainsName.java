
package commands;

import collections.Flat;
import storage.Storage;

/**
 * The FilterContainsName class filters flats by a substring in their name.
 */
public class FilterContainsName {
    /**
     * Displays flats whose names contain the given substring.
     * @param storage the storage containing flats
     * @param commandArguments the command arguments containing the substring
     */
    public FilterContainsName(Storage storage, String[] commandArguments) {
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

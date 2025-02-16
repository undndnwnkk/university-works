package commands;

import storage.Storage;

/**
 * The Sort class sorts the storage of flats in alphabetical order by name.
 */
public class Sort {
    /**
     * Sorts the flats in storage alphabetically by name.
     * @param storage the storage containing flats
     */
    public Sort(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Nothing to sort :(");
            return;
        }
        storage.getFlatStorage().sort((flat1, flat2) -> flat1.getName().compareTo(flat2.getName()));
        System.out.println("Sorted " + storage.getFlatStorage().size() + " items");
    }
}

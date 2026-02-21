package commands;

import collections.Flat;
import storage.Storage;

import java.util.Vector;

/**
 * The RemoveById class removes a flat from storage by its ID.
 */
public class RemoveById {
    /**
     * Removes a flat with the given ID.
     * @param storage the storage object containing flats
     * @param commandArguments the command arguments containing the ID
     */
    public RemoveById(Storage storage, String[] commandArguments) {
        if (commandArguments.length == 0) {
            System.out.println("Error! No ID provided.");
            return;
        }

        try {
            int idToRemove = Integer.parseInt(commandArguments[0]);
            Vector<Flat> storageElements = storage.getFlatStorage();
            int indexToRemove = -1;

            for (int i = 0; i < storageElements.size(); i++) {
                if (storageElements.get(i).getId() == idToRemove) {
                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                storageElements.remove(indexToRemove);
                System.out.println("Removed flat with ID " + idToRemove);
            } else {
                System.out.println("Error! No flat found with ID " + idToRemove);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error! ID must be an integer.");
        }
    }
}

package commands;

import collections.Flat;
import storage.Storage;

import java.util.Vector;

public class RemoveById {
    public RemoveById(Storage storage, String[] commandArguments) {
        if (commandArguments.length == 0) {
            System.out.println("Error! No ID provided.");
            return;
        }

        try {
            int idToRemove = Integer.parseInt(commandArguments[0]);
            Vector<Flat> storageElements = storage.getFlatStorage();
            int indexToRemove = -123;

            for (int i = 0; i < storageElements.size(); i++) {
                if (storageElements.get(i).getId() == idToRemove) {
                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -123) {
                storageElements.remove(indexToRemove);
                System.out.println("Removed flat with id " + idToRemove);
            } else {
                System.out.println("Error! No flat found with ID " + idToRemove);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error! ID must be an integer.");
        }
    }
}

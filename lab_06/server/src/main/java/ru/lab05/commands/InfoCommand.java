package ru.lab05.commands;

import ru.lab05.core.Storage;

/**
 * The Info class displays information about the current collection.
 */
public class InfoCommand {
    /**
     * Prints information about the collection.
     * @param storage the storage object containing flats
     */
    public InfoCommand(Storage storage) {
        System.out.println("Collection information:");
        System.out.println("Type of collection: " + storage.getClass().getSimpleName());
        System.out.println("Initialization date: " + storage.getInitializationDate());
        System.out.println("Number of elements: " + storage.getFlatStorage().size());
    }

    public static String execute(Storage storage) {
        return "\nCollection information:\n" +
                "Type of collection: " + storage.getClass().getSimpleName() + "\n" +
                "Initialization date: " + storage.getInitializationDate() + "\n" +
                "Number of elements: " + storage.getFlatStorage().size() + "\n";
    }
}

package ru.lab05.commands;

import ru.lab05.model.Flat;
import ru.lab05.core.Storage;

/**
 * The FilterByTransport class filters flats by transport type.
 */
public class FilterByTransportCommand {
    /**
     * Displays flats that match the specified transport type.
     * @param storage the storage containing flats
     * @param commandArguments the command arguments containing transport type
     */
    public FilterByTransportCommand(Storage storage, String[] commandArguments) {
        if (storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
        } else {
            System.out.println("Elements with transport type " + commandArguments[0] + ":");
            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getTransport() != null &&
                        flat.getTransport().toString().equals(commandArguments[0].toUpperCase())) {
                    System.out.println(flat);
                }
            }
        }
    }
}

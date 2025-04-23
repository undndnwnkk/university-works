package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;

import java.util.Collections;
import java.util.Vector;

/**
 * The MinById class finds and prints the flat with the smallest ID.
 */
public class MinById implements Command {
    /**
     * Prints the flat with the smallest ID.
     * @param storage the storage object containing flats
     */
    @Override
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty");
        }

        Vector<Flat> storageElementsCopy = new Vector<>(storage.getFlatStorage());
        Collections.sort(storageElementsCopy, (flat1, flat2) -> Integer.compare(flat1.getId(), flat2.getId()));
        return new CommandResponse("Element with smallest ID: " + storageElementsCopy.get(0).toString());
    }
}

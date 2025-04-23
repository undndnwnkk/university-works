package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;

import java.util.Collections;

/**
 * The Reorder class reverses the order of elements in the storage.
 */
public class Reorder implements Command {
    /**
     * Reverses the order of flats in the storage.
     * @param storage the storage object containing flats
     */

    @Override
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty, nothing to reverse :(");
        } else {
            Collections.reverse(storage.getFlatStorage());
            return new CommandResponse("Collection reordered successfully!");
        }
    }
}

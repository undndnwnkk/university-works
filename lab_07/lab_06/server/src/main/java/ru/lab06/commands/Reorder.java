package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

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
    public CommandResponse execute(StorageLike storage, String loginNew) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty, nothing to reverse :(");
        } else {
            Collections.reverse(storage.getFlatStorage());
            return new CommandResponse("Collection reordered successfully!");
        }
    }
}

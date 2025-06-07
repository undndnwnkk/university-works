package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

import java.util.Comparator;
import java.util.Vector;

/**
 * The Show class displays all elements in the collection.
 */
public class Show implements Command {
    /**
     * Prints all flats stored in the collection.
     * @param storage the storage object containing flats
     */

    @Override
    public CommandResponse execute(StorageLike storage) {
        String result = storage.getFlatStorage().stream()
                .sorted(Comparator.comparing(Flat::getName))
                .map(Flat::toString)
                .reduce("", (a, b) -> a + b + "\n");

        if (result.isEmpty()) {
            return new CommandResponse("Collection is empty.");
        }

        return new CommandResponse(result.trim());
    }

}

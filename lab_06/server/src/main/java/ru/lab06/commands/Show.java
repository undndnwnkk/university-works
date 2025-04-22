package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;

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
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Storage is empty :(");
        }

        StringBuilder output = new StringBuilder("All flats:\n");
        for (Flat flat : storage.getFlatStorage()) {
            output.append(flat.toString()).append("\n");
        }

        return new CommandResponse(output.toString());
    }
}

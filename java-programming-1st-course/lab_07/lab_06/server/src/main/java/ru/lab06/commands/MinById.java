package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

import java.util.Collections;
import java.util.Comparator;
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
    public CommandResponse execute(StorageLike storage, String loginNew) {
        return storage.getFlatStorage().stream()
                .min(Comparator.comparingInt(Flat::getId))
                .map(flat -> new CommandResponse(flat.toString()))
                .orElse(new CommandResponse("Collection is empty"));
    }

}

package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

import java.util.Comparator;

/**
 * The Sort class sorts the storage of flats in alphabetical order by name.
 */
public class Sort implements Command {
    /**
     * Sorts the flats in storage alphabetically by name.
     * @param storage the storage containing flats
     */
    @Override
    public CommandResponse execute(StorageLike storage) {
        String result = storage.getFlatStorage().stream()
                .sorted(Comparator.comparing(Flat::getName))
                .map(Flat::toString)
                .reduce("", (a, b) -> a + b + "\n");

        if (result.isEmpty()) return new CommandResponse("Collection is empty");

        return new CommandResponse(result.trim());
    }

}

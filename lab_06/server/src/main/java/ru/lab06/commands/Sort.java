package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;

/**
 * The Sort class sorts the storage of flats in alphabetical order by name.
 */
public class Sort implements Command {
    /**
     * Sorts the flats in storage alphabetically by name.
     * @param storage the storage containing flats
     */
    @Override
    public CommandResponse execute(Storage storage) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Nothing to sort :(");
        }
        storage.getFlatStorage().sort((flat1, flat2) -> flat1.getName().compareTo(flat2.getName()));
        return new CommandResponse("Sorted" + storage.getFlatStorage().size() + " items");
    }
}

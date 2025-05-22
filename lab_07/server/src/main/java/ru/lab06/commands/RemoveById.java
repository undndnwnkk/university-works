package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

import java.util.Vector;

/**
 * The RemoveById class removes a flat from storage by its ID.
 */
public class RemoveById implements Command {
    /**
     * Removes a flat with the given ID.
     * @param storage the storage object containing flats
     * @param commandArguments the command arguments containing the ID
     */
    private Object[] commandArguments;

    public RemoveById(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        if (commandArguments.length == 0) {
            return new CommandResponse("Error, no ID was written");
        }

        try {
            int idToRemove = Integer.parseInt(commandArguments[0].toString());
            Vector<Flat> storageElements = storage.getFlatStorage();
            int indexToRemove = -1;

            for (int i = 0; i < storageElements.size(); i++) {
                if (storageElements.get(i).getId() == idToRemove) {
                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                storageElements.remove(indexToRemove);
                return new CommandResponse("Removed flat with ID " + idToRemove);
            } else {
                return new CommandResponse("Error, no flat found with ID " + idToRemove);
            }
        } catch (NumberFormatException e) {
            return new CommandResponse("Error! ID must be an integer.");
        }
    }

    public RemoveById(Storage storage, String[] commandArguments) {

    }
}

package ru.lab06.commands;

import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.db.FlatDaoImpl;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

public class RemoveById implements Command {
    private final Object[] commandArguments;
    private final FlatDaoImpl flatDao = new FlatDaoImpl();

    public RemoveById(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        try {
            if (commandArguments.length != 1) {
                return new CommandResponse("Error: You must provide exactly one argument (id).");
            }

            int id = Integer.parseInt(commandArguments[0].toString());

            Flat flat = storage.getFlatStorage().stream()
                    .filter(f -> f.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (flat == null) {
                return new CommandResponse("Flat with id " + id + " not found.");
            }

            if (!login.equals(flat.getOwner())) {
                return new CommandResponse("Access denied. You are not the owner.");
            }

            int ownerId = UserManager.getUserId(login);
            if (!flatDao.removeById(id, ownerId)) {
                return new CommandResponse("Error: Failed to remove flat from database.");
            }

            storage.getFlatStorage().removeIf(f -> f.getId() == id);
            return new CommandResponse("Flat removed successfully.");
        } catch (Exception e) {
            return new CommandResponse("Error: " + e.getMessage());
        }
    }
}

package ru.lab06.commands;

import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.db.FlatDaoImpl;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

public class RemoveFirst implements Command {
    private final FlatDaoImpl flatDao = new FlatDaoImpl();

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        if (storage.getFlatStorage().isEmpty()) {
            return new CommandResponse("Collection is empty.");
        }

        Flat first = storage.getFlatStorage().get(0);

        if (!login.equals(first.getOwner())) {
            return new CommandResponse("Access denied. You are not the owner of the first flat.");
        }

        int ownerId = UserManager.getUserId(login);
        if (!flatDao.removeById(first.getId(), ownerId)) {
            return new CommandResponse("Error: Could not delete from database.");
        }

        storage.getFlatStorage().remove(0);
        return new CommandResponse("First flat removed successfully.");
    }
}

package ru.lab06.commands;

import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.db.FlatDaoImpl;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

import java.util.Iterator;

public class Clear implements Command {
    private final FlatDaoImpl flatDao = new FlatDaoImpl();

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        int ownerId = UserManager.getUserId(login);
        if (ownerId == -1) {
            return new CommandResponse("Error: user not found in database");
        }

        Iterator<Flat> iterator = storage.getFlatStorage().iterator();
        boolean removedAny = false;

        while (iterator.hasNext()) {
            Flat flat = iterator.next();
            if (login.equals(flat.getOwner())) {
                if (flatDao.removeById(flat.getId(), ownerId)) {
                    iterator.remove();
                    removedAny = true;
                }
            }
        }

        if (removedAny) {
            return new CommandResponse("Your flats were successfully removed.");
        } else {
            return new CommandResponse("You have no flats to remove.");
        }
    }
}

package ru.lab06.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.lab06.ServerApp;
import ru.lab06.auth.UserManager;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.db.FlatDaoImpl;
import ru.lab06.model.Flat;
import ru.lab06.storage.StorageLike;

public class Add implements Command {
    private final Object[] commandArguments;
    private final Logger logger = (Logger) LogManager.getLogger(ServerApp.class);
    private final FlatDaoImpl flatDao = new FlatDaoImpl();

    public Add(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        try {
            if (commandArguments.length < 1 || !(commandArguments[0] instanceof Flat flat)) {
                return new CommandResponse("Error: Flat object was not provided.");
            }

            flat.setId(storage.getCurrentId());
            flat.setCreationDate(new java.util.Date());
            flat.setOwner(login);

            Integer ownerId = UserManager.getUserId(login);
            if (ownerId == null) return new CommandResponse("Error: user not found in database");

            boolean inserted = flatDao.insertFlat(flat, ownerId);
            if (!inserted) {
                return new CommandResponse("Error: Could not add flat to the database.");
            }

            storage.setCurrentId(storage.getCurrentId() + 1);
            storage.getFlatStorage().add(flat);

            logger.info("Flat added successfully by user " + login);
            return new CommandResponse("Flat added successfully.");
        } catch (Exception e) {
            return new CommandResponse("Error: " + e.getMessage());
        }
    }
}

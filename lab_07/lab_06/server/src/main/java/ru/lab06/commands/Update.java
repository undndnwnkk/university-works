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

import java.util.Vector;

public class Update implements Command {
    private final Object[] commandArguments;
    private final Logger logger = (Logger) LogManager.getLogger(ServerApp.class);
    private final FlatDaoImpl flatDao = new FlatDaoImpl();

    public Update(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        try {
            if (commandArguments.length < 2 || !(commandArguments[0] instanceof Integer id) || !(commandArguments[1] instanceof Flat flat)) {
                return new CommandResponse("Error: Expected arguments (Integer id, Flat object).");
            }

            flat.setId(id);

            Integer ownerId = UserManager.getUserId(login);
            if (ownerId == null) return new CommandResponse("Error: user not found");

            Vector<Flat> flats = storage.getFlatStorage();
            Flat existing = flats.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
            if (existing == null) return new CommandResponse("Update failed: no such flat.");
            if (!existing.getOwner().equals(login)) return new CommandResponse("Update failed: you are not the owner of this flat.");

            boolean success = flatDao.updateFlatById(id, flat, ownerId);
            if (!success) return new CommandResponse("Update failed: database update failed.");

            flats.remove(existing);
            flat.setOwner(login);
            flats.add(flat);

            logger.info("Flat with ID " + id + " updated by user " + login);
            return new CommandResponse("Flat updated successfully.");
        } catch (Exception e) {
            return new CommandResponse("Error: " + e.getMessage());
        }
    }
}

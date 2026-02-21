package ru.lab06.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.lab06.ServerApp;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.*;
import ru.lab06.storage.StorageLike;
import ru.lab06.core.Writer;

public class Update implements Command {

    private final Object[] commandArguments;
    private final Logger logger = (Logger) LogManager.getLogger(ServerApp.class);

    public Update(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        try {
            int id = Integer.parseInt(commandArguments[0].toString());
            Flat targetFlat = null;

            for (Flat flat : storage.getFlatStorage()) {
                if (flat.getId() == id) {
                    targetFlat = flat;
                    break;
                }
            }

            if (targetFlat == null) {
                return new CommandResponse("This flat does not exist");
            }

            targetFlat.setName((String) commandArguments[1]);
            targetFlat.setCoordinates((Coordinates) commandArguments[2]);
            targetFlat.setArea((Integer) commandArguments[3]);
            targetFlat.setNumberOfRooms((Long) commandArguments[4]);
            targetFlat.setFurnish((Furnish) commandArguments[5]);
            targetFlat.setView((View) commandArguments[6]);
            targetFlat.setTransport((Transport) commandArguments[7]);
            targetFlat.setHouse((House) commandArguments[8]);

            Writer.writeJson(storage.getFilename(), storage.getFlatStorage());
            logger.info("Flat with id={} updated successfully", id);
            return new CommandResponse("Flat with ID " + id + " added successfully.");

        } catch (Exception e) {
            return new CommandResponse("Error while updating:  " + e.getMessage());
        }
    }
}
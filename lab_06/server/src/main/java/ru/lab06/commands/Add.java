package ru.lab06.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.lab06.ServerApp;
import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Writer;
import ru.lab06.model.*;
import ru.lab06.storage.StorageLike;

import java.util.Date;

public class Add implements Command {
    private Object[] commandArguments;
    private final Logger logger = (Logger) LogManager.getLogger(ServerApp.class);
    public Add(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        try {
            Flat flat = new Flat();
            flat.setName(commandArguments[0].toString());
            flat.setCoordinates((Coordinates) commandArguments[1]);
            flat.setArea((Integer) commandArguments[2]);
            flat.setNumberOfRooms((Long) commandArguments[3]);
            flat.setFurnish((Furnish) commandArguments[4]);
            flat.setView((View) commandArguments[5]);
            flat.setTransport((Transport) commandArguments[6]);
            flat.setHouse((House) commandArguments[7]);
            flat.setId(storage.getCurrentId());
            flat.setCreationDate(new Date());
            storage.setCurrentId(storage.getCurrentId() + 1);

            storage.getFlatStorage().add(flat);
            Writer.writeJson(storage.getFilename(), storage.getFlatStorage());
            logger.info("Flat added succesfully ", flat.getName());
            return new CommandResponse("Flat added successfully");
        } catch (Exception e) {
            return new CommandResponse("Error: " + e.getMessage());
        }
    }
}
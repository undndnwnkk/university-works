package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.model.*;

import java.util.Date;

public class Add implements Command {
    private Object[] commandArguments;

    public Add(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(Storage storage) {
        try {
            Flat flat = new Flat();
            flat.setName(commandArguments[0].toString());
            flat.setCoordinates((Coordinates) commandArguments[1]);
            flat.setArea((Integer) commandArguments[2]);
            flat.setNumberOfRooms((Integer) commandArguments[3]);
            flat.setFurnish((Furnish) commandArguments[4]);
            flat.setView((View) commandArguments[5]);
            flat.setTransport((Transport) commandArguments[6]);
            flat.setHouse((House) commandArguments[7]);
            flat.setId(storage.getCurrentId());
            flat.setCreationDate(new Date());
            storage.setCurrentId(storage.getCurrentId() + 1);

            storage.getFlatStorage().add(flat);

            return new CommandResponse("Flat added successfully");
        } catch (Exception e) {
            return new CommandResponse("Error: " + e.getMessage());
        }
    }
}
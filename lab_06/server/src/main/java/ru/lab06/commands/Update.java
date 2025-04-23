package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.model.*;

public class Update implements Command {

    private final Object[] commandArguments;

    public Update(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(Storage storage) {
        try {
            int id = (Integer) commandArguments[0];
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

            return new CommandResponse("Квартира с ID " + id + " успешно обновлена.");

        } catch (Exception e) {
            return new CommandResponse("Ошибка при обновлении квартиры: " + e.getMessage());
        }
    }
}
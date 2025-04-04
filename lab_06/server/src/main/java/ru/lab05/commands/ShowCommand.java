package ru.lab05.commands;

import ru.lab05.core.Storage;
import ru.lab05.model.Flat;

import java.util.Comparator;
import java.util.Vector;

public class ShowCommand {
    private final Storage storage;

    public ShowCommand(Storage storage) {
        this.storage = storage;
    }

    public String execute() {
        StringBuilder sb = new StringBuilder();
        Vector<Flat> flats = storage.getFlatStorage();

        if (flats.isEmpty()) {
            sb.append("Collection is empty.");
        } else {
            flats.stream()
                    .sorted(Comparator.comparing(Flat::getName))
                    .forEach(flat -> sb.append(flat.toString()).append("\n"));
        }

        return sb.toString();
    }
}

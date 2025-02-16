package commands;

import collections.Flat;
import storage.Storage;
import readWrite.Reader;
import readWrite.Writer;

import java.util.Arrays;
import java.util.Vector;

public class Update {
    private final Storage storage;

    // Конструктор для обновления по JSON
    public Update(Storage storage, String[] arguments) {
        this.storage = storage;

        if (arguments.length < 2) {
            System.out.println("Error! Usage: update id {element}");
            return;
        }

        try {
            int id = Integer.parseInt(arguments[0]);
            String jsonElement = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
            Flat updatedFlat = Reader.parseJsonFromString(jsonElement, Flat.class);

            if (updatedFlat == null) {
                System.out.println("Error! Invalid JSON format.");
                return;
            }

            updateFlat(id, updatedFlat);
        } catch (NumberFormatException e) {
            System.out.println("Error! ID must be an integer.");
        }
    }

    public Update(Storage storage, int id, Flat updatedFlat) {
        this.storage = storage;
        updateFlat(id, updatedFlat);
    }

    private void updateFlat(int id, Flat updatedFlat) {
        Vector<Flat> flats = storage.getFlatStorage();
        for (int i = 0; i < flats.size(); i++) {
            if (flats.get(i).getId() == id) {
                updatedFlat.setId(id);
                updatedFlat.setCreationDate(flats.get(i).getCreationDate());
                flats.set(i, updatedFlat);
                System.out.println("Flat with ID " + id + " updated successfully.");
                Writer.writeJson(storage.getFilename(), flats);
                return;
            }
        }
        System.out.println("Error! No flat found with ID " + id);
    }
}

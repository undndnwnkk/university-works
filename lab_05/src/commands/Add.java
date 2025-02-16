package commands;

import collections.Flat;
import storage.Storage;
import readWrite.Reader;
import java.util.Date;

public class Add {
    private final Storage storage;

    public Add(Storage storage, String[] arguments) {
        this.storage = storage;

        if (arguments.length == 0) {
            System.out.println("Error! No element provided.");
            return;
        }

        try {
            String jsonElement = String.join(" ", arguments);
            Flat newFlat = Reader.parseJsonFromString(jsonElement, Flat.class);

            if (newFlat == null) {
                System.out.println("Error! Invalid JSON format. Trying to interpret as Java object...");
                return;
            }

            processFlat(newFlat);
        } catch (Exception e) {
            System.out.println("Error! Invalid JSON format. Trying to interpret as Java object...");
        }
    }

    public Add(Storage storage, Flat flat) {
        this.storage = storage;
        processFlat(flat);
    }

    private void processFlat(Flat flat) {
        if (flat == null || flat.getName() == null || flat.getName().isEmpty() ||
                flat.getCoordinates() == null || flat.getArea() <= 0 ||
                flat.getNumberOfRooms() <= 0) {
            System.out.println("Error! Invalid flat data.");
            return;
        }

        flat.setId(generateUniqueId());
        flat.setCreationDate(new Date());
        storage.getFlatStorage().add(flat);
        System.out.println("Flat added successfully!");
    }

    private int generateUniqueId() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}

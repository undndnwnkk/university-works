package commands;

import collections.Flat;
import collections.Coordinates;
import collections.House;
import collections.Furnish;
import collections.View;
import collections.Transport;
import storage.Storage;
import java.util.Date;
import java.util.Scanner;

/**
 * The Add class handles adding new Flat objects to the storage.
 */
public class Add {
    private final Storage storage;

    /**
     * Constructs an Add command and reads user input to add a new Flat.
     * @param storage the storage to which the flat is added
     * @param arguments command arguments
     */
    public Add(Storage storage, String[] arguments) {
        this.storage = storage;
        Flat newFlat = readFlatFromConsole();
        if (newFlat != null) {
            processFlat(newFlat);
        }
    }

    /**
     * Adds a predefined Flat object to the storage.
     * @param storage the storage to which the flat is added
     * @param flat the flat to be added
     */
    public Add(Storage storage, Flat flat) {
        this.storage = storage;
        this.storage.setCurrentId(storage.getCurrentId() + 1);
        processFlat(flat);
    }

    /**
     * Reads flat attributes from user input via the console.
     * @return a new Flat object with the provided attributes
     */
    private Flat readFlatFromConsole() {
        Scanner scanner = new Scanner(System.in);
        Flat flat = new Flat();

        System.out.print("Enter flat name (non-empty): ");
        flat.setName(scanner.nextLine().trim());

        Coordinates coordinates = new Coordinates();
        System.out.print("Enter coordinate X (float): ");
        coordinates.setX(Float.parseFloat(scanner.nextLine().trim()));
        System.out.print("Enter coordinate Y (must be greater than -46): ");
        coordinates.setY(Float.parseFloat(scanner.nextLine().trim()));
        flat.setCoordinates(coordinates);

        return flat;
    }

    /**
     * Validates and adds a flat to storage.
     * @param flat the flat to be added
     */
    private void processFlat(Flat flat) {
        if (flat == null || flat.getName() == null || flat.getName().isEmpty() ||
                flat.getCoordinates() == null || flat.getArea() == null || flat.getArea() <= 0 ||
                flat.getNumberOfRooms() <= 0 || flat.getFurnish() == null || flat.getView() == null) {
            System.out.println("Error! Invalid flat data.");
            return;
        }

        flat.setId(storage.getCurrentId());
        storage.setCurrentId(storage.getCurrentId() + 1);

        flat.setCreationDate(new Date());
        storage.getFlatStorage().add(flat);
        System.out.println("Flat added successfully!");
    }
}

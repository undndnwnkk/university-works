package storage;

import collections.Flat;
import readWrite.Reader;
import validation.Validation;

import java.util.Date;
import java.util.Vector;

/**
 * The {@code Storage} class manages the collection of {@code Flat} objects.
 * It loads data from a JSON file, ensures its validity, and provides methods to interact with the collection.
 */
public class Storage {
    private Vector<Flat> flatStorage;
    private final String filename;
    private final Date initializationDate;
    private int currentId = 1;

    /**
     * Constructs a {@code Storage} instance and loads data from the specified JSON file.
     *
     * @param filename the name of the JSON file to load data from.
     */
    public Storage(String filename) {
        this.filename = filename;
        this.flatStorage = new Vector<>();
        this.initializationDate = new Date();
        loadFromFile();
    }

    /**
     * Loads data from the JSON file, validates it, and adds only valid objects to the collection.
     * Also, determines the next available ID based on the highest existing ID.
     */
    public void loadFromFile() {
        Validation validation = new Validation(filename);
        this.flatStorage = validation.getValidFlats();

        if (!flatStorage.isEmpty()) {
            System.out.println("Loaded " + flatStorage.size() + " valid flats.");
            int maxId = flatStorage.stream().mapToInt(Flat::getId).max().orElse(0);
            this.currentId = maxId + 1;
        } else {
            System.out.println("No valid flats found.");
        }
    }

    /**
     * Prints information about the collection, including type, initialization date, and number of elements.
     */
    public void printCollectionInfo() {
        System.out.println("Collection information:");
        System.out.println("Type of collection: Vector");
        System.out.println("Initialization date: " + initializationDate);
        System.out.println("Number of elements: " + flatStorage.size());
    }

    /**
     * Returns the initialization date of the collection.
     *
     * @return the date when the collection was initialized.
     */
    public Date getInitializationDate() {
        return initializationDate;
    }

    /**
     * Returns the filename associated with this storage.
     *
     * @return the name of the JSON file.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the collection of {@code Flat} objects.
     *
     * @return a {@code Vector} containing all valid {@code Flat} objects.
     */
    public Vector<Flat> getFlatStorage() {
        return flatStorage;
    }

    /**
     * Returns the current available ID for new objects.
     *
     * @return the next available ID.
     */
    public Integer getCurrentId() {
        return currentId;
    }

    /**
     * Sets the current ID value, which is used for assigning new IDs.
     *
     * @param currentId the new ID value.
     */
    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
    }
}

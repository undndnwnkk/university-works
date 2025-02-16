package storage;

import collections.Flat;
import readWrite.Reader;

import java.util.Date;
import java.util.Vector;

/**
 * The Storage class represents a storage system for Flat objects.
 * It provides functionalities to load, store, and manage flats.
 */
public class Storage {
    private Vector<Flat> flatStorage;
    private final String filename;
    private final Date initializationDate;
    private int currentId = 1;

    /**
     * Constructs a Storage instance and loads data from the specified file.
     * @param filename the file to load flats from
     */
    public Storage(String filename) {
        this.filename = filename;
        this.flatStorage = new Vector<>();
        this.initializationDate = new Date();
        loadFromFile();
    }

    /**
     * Loads flats from the file and updates the storage.
     */
    public void loadFromFile() {
        Vector<Flat> loadedFlats = Reader.readJson(filename);
        if (loadedFlats != null) {
            this.flatStorage = loadedFlats;
            System.out.println("Loaded " + loadedFlats.size() + " flats");

            int maxId = 0;
            for (Flat flat : loadedFlats) {
                if (flat.getId() > maxId) {
                    maxId = flat.getId();
                }
            }
            this.currentId = maxId + 1;
        } else {
            System.out.println("No file found");
        }
    }

    /**
     * Prints information about the current storage collection.
     */
    public void printCollectionInfo() {
        System.out.println("Collection information:");
        System.out.println("Type of collection: Vector");
        System.out.println("Initialization date: " + initializationDate);
        System.out.println("Number of elements: " + flatStorage.size());
    }

    /**
     * It's date getter
     * @return the date when the storage was initialized
     */
    public Date getInitializationDate() {
        return initializationDate;
    }

    /**
     * It's filename getter
     * @return the filename where flats are stored
     */
    public String getFilename() {
        return filename;
    }

    /**
     * It's collection getter
     * @return the collection of stored flats
     */
    public Vector<Flat> getFlatStorage() {
        return flatStorage;
    }

    /**
     * It's current ID getter
     * @return the current ID counter for new flats
     */
    public Integer getCurrentId() {
        return currentId;
    }

    /**
     * Updates the current ID counter.
     * @param currentId the new ID counter value
     */
    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
    }
}

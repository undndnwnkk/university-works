package storage;

import collections.Flat;
import readWrite.Reader;

import java.util.Date;
import java.util.Vector;

public class Storage {
    private Vector<Flat> flatStorage;
    private final String filename;
    private final Date initializationDate;
    private int currentId = 1;

    public Storage(String filename) {
        this.filename = filename;
        this.flatStorage = new Vector<>();
        this.initializationDate = new Date();
        loadFromFile();
    }

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

    public void printCollectionInfo() {
        System.out.println("Collection information:");
        System.out.println("Type of collection: Vector");
        System.out.println("Initialization date: " + initializationDate);
        System.out.println("Number of elements: " + flatStorage.size());
    }

    public Date getInitializationDate() {
        return initializationDate;
    }

    public String getFilename() {
        return filename;
    }

    public Vector<Flat> getFlatStorage() {
        return flatStorage;
    }

    public Integer getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
    }
}

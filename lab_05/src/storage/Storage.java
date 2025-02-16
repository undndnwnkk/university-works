package storage;

import collections.Flat;
import readWrite.Reader;

import java.util.Date;
import java.util.Vector;

public class Storage {
    private Vector<Flat> flatStorage;
    private final String filename;
    private final Date initializationDate;

    public Storage(String filename) {
        this.filename = filename;
        this.flatStorage = new Vector<>();
        this.initializationDate = new Date();
        loadFromFile();
    }

    public void loadFromFile() {
        Vector<Flat> loadedFlats = Reader.readJson(filename, Vector.class);
        if(loadedFlats != null) {
            this.flatStorage = loadedFlats;
            System.out.println("Loaded " + loadedFlats.size() + " flats");
        } else {
            System.out.println("No file found");
        }
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
}

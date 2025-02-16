package commands;

import storage.Storage;
import readWrite.Writer;

/**
 * The Save class writes the current storage collection to a file.
 */
public class Save {
    /**
     * Saves the current storage collection to a file.
     * @param storage the storage object containing flats
     * @param filename the name of the file to save to
     */
    public Save(Storage storage, String filename) {
        if (!storage.getFlatStorage().isEmpty()) {
            Writer.writeJson(filename, storage.getFlatStorage());
            System.out.println("Saved successfully!");
        } else {
            System.out.println("Nothing to save :(");
        }
    }
}

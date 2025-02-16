package commands;

import storage.Storage;
import readWrite.Writer;

public class Save {
    public Save(Storage storage, String filename) {
        if(!storage.getFlatStorage().isEmpty()) {
            Writer.writeJson(filename, storage.getFlatStorage());
            System.out.println("Saved successfully!");
        } else {
            System.out.println("Nothing to save :(");
        }
    }
}
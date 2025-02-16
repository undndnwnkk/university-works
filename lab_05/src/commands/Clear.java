package commands;

import storage.Storage;

public class Clear {
    public Clear(Storage storage) {
        storage.getFlatStorage().clear();
    }
}
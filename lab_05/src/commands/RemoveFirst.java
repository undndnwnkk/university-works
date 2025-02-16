package commands;

import storage.Storage;

public class RemoveFirst {
    public RemoveFirst(Storage storage) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Nothig to remove!");
        } else {
            storage.getFlatStorage().remove(0);
            System.out.println("First element removed successfully!");
        }
    }
}
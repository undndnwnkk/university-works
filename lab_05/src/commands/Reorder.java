package commands;

import storage.Storage;

import java.util.Collection;
import java.util.Collections;

public class Reorder {
    public Reorder(Storage storage) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty, nothing to reverse :(");
        } else {
            Collections.reverse(storage.getFlatStorage());
            System.out.println("Reverse successfully!");
        }
    }
}
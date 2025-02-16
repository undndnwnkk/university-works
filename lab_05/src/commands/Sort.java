package commands;

import storage.Storage;

import java.util.Collections;

public class Sort {
    public Sort(Storage storage) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Nothing to sort :(");
            return;
        } else {
            storage.getFlatStorage().sort((flat1, flat2) -> flat1.getName().compareTo(flat2.getName()));
            System.out.println("Sorted " + storage.getFlatStorage().size() + " items");
        }
    }
}
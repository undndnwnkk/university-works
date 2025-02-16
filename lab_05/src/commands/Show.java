package commands;

import collections.Flat;
import storage.Storage;

import java.util.Vector;

public class Show {
    public Show(Storage storage) {
        Vector<Flat> storageElements = storage.getFlatStorage();

        if(storageElements.isEmpty()) {
            System.out.println("No elements in storage");
        } else {
            for(Flat element : storageElements) {
                System.out.println(element);
            }
        }
    }
}
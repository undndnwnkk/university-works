package commands;

import collections.Flat;
import storage.Storage;

import java.util.Collections;
import java.util.Vector;
import commands.Sort;

public class MinById {
    public MinById(Storage storage) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
            return;
        } else {
            Vector<Flat> storageElementsCopy = storage.getFlatStorage();
            Collections.sort(storageElementsCopy, (flat1, flat2) -> Integer.compare(flat1.getId(), flat2.getId()));
            System.out.println("Element with smallest id is " + storageElementsCopy.get(0));
        }
    }
}
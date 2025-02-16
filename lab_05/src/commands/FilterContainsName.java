package commands;

import collections.Flat;
import storage.Storage;

public class FilterContainsName {
    public FilterContainsName(Storage storage, String[] commandArguments) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
        } else {
            for(Flat flat : storage.getFlatStorage()) {
                if(flat.getName().toLowerCase().contains(commandArguments[0].toLowerCase())) {
                    System.out.println(flat);
                }
            }
        }
    }
}
package commands;

import collections.Flat;
import storage.Storage;

public class FilterByTransport {
    public FilterByTransport(Storage storage, String[] commandArguments) {
        if(storage.getFlatStorage().isEmpty()) {
            System.out.println("Storage is empty!");
        } else {
            System.out.println("Elements with transport type " + commandArguments[0] + ":");
            for(Flat flat : storage.getFlatStorage()) {
                if(flat.getTransport().equals(commandArguments[0])) {
                    System.out.println(flat);
                }
            }
        }
    }
}
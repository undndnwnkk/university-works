package commands;

import storage.Storage;

public class Info {
    public Info(Storage storage) {
        System.out.println("Information about collection: ");
        System.out.println("Type of collection: " + storage.getClass().getSimpleName());
        System.out.println("Date of initialization " + storage.getInitializationDate());
        System.out.println("Count of elements: " + storage.getFlatStorage().size());
    }
}
package application;

import storage.Storage;

import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Main class is the entry point of the application.
 */
public class Main {
    /**
     * Main constructor(nothing to do)
     */
    public Main() {}
    /**
     * To compile: javac -d out -cp src/libraries/gson-2.12.1.jar -sourcepath src $(find src -name "*.java")
     * Create jar: jar cfm app.jar /home/undndnwnkk/development/java/itmo-labs/lab_05/src/META-INF/MANIFEST.mf -C out .
     * Start program: java -cp app.jar:src/libraries/gson-2.12.1.jar application.Main test.json
     * @param args command-line arguments (expects a filename as an argument)
    */
     public static void main(String[] args) {
        String filename;
        if (args.length == 0) {
            System.out.println("Error! No arguments provided!");
            return;
        } else {
            filename = args[0];
            File file = new File(filename);

            if (Files.notExists(file.toPath())) {
                System.out.println("Error! File not found!");
            }
            if (Files.isDirectory(file.toPath())) {
                System.out.println("Error! It's a directory!");
                return;
            } else {
                if (Files.isReadable(file.toPath()) && Files.isWritable(file.toPath())) {
                    System.out.println("Ok! Will use this file");
                } else {
                    System.out.println("Error! No permission to write or read this file");
                    return;
                }
            }
        }
        Storage storage = new Storage(filename);
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.print("\nPlease enter a command: ");
                if(!scanner.hasNextLine()) {
                    System.out.println("\nUse 'exit' command to end a program");
                    scanner = new Scanner(System.in);
                    continue;
                }
                String command = scanner.nextLine();
                new CommandsList(storage, command, filename);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Unknown command!");
        }
    }
}

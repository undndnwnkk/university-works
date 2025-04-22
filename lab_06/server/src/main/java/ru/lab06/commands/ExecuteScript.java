package ru.lab06.commands;

import ru.lab06.core.Storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The ExecuteScript class reads and executes commands from a file.
 */
public class ExecuteScript {
    /**
     * Executes commands from a specified script file.
     * @param storage the storage object used in commands
     * @param commandArguments the command arguments containing the script filename
     */
    public ExecuteScript(Storage storage, String[] commandArguments) {
        if (commandArguments.length == 0) {
            System.out.println("Error! No script filename provided.");
            return;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(commandArguments[0]))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                currentLine = currentLine.trim();
                if (!currentLine.isEmpty()) {
                    System.out.println("Executing command: " + currentLine);
                    new CommandsList(storage, currentLine, storage.getFilename());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error! File not found.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
}

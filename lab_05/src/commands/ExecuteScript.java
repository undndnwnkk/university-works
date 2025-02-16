package commands;

import application.CommandsList;
import storage.Storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExecuteScript {
    public ExecuteScript(Storage storage, String[] commandArguments) {
        if(commandArguments.length == 0) {
            System.out.println("Enough arguments to execute script");
            return;
        }

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(commandArguments[0]))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                currentLine.trim();
                if(!currentLine.isEmpty()) {
                    System.out.println("Executing command " + currentLine);
                    new CommandsList(storage, currentLine, storage.getFilename());
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
}
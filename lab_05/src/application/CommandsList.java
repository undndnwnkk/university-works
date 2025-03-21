package application;

import java.util.Arrays;
import commands.*;
import storage.Storage;

/**
 * The CommandsList class processes user commands and executes them accordingly.
 */
public class CommandsList {
    private final String[] commands = {
            "help",
            "info",
            "show",
            "add",
            "update",
            "remove_by_id",
            "clear",
            "save",
            "execute_script",
            "exit",
            "remove_first",
            "reorder",
            "sort",
            "min_by_id",
            "filter_by_transport",
            "filter_contains_name"
    };
    private String currentCommand;
    private String[] commandArguments;

    /**
     * Splits input into command and arguments.
     * @param input the user input string
     */
    private void getCommandAndArguments(String input) {
        input = input.trim();
        String[] commandAndArguments = input.split("\\s+");
        currentCommand = commandAndArguments[0];
        commandArguments = Arrays.copyOfRange(commandAndArguments, 1, commandAndArguments.length);
    }

    /**
     * Executes the appropriate command based on user input.
     * @param storage the storage object containing flats
     * @param input the user command input
     * @param filename the filename where data is stored
     */
    public CommandsList(Storage storage, String input, String filename) {
        getCommandAndArguments(input);

        switch (currentCommand) {
            case "help" -> new Help();
            case "info" -> new Info(storage);
            case "show" -> new Show(storage);
            case "add" -> new Add(storage);
            case "update" -> new Update(storage, commandArguments);
            case "remove_by_id" -> new RemoveById(storage, commandArguments);
            case "clear" -> new Clear(storage);
            case "save" -> new Save(storage, filename);
            case "execute_script" -> new ExecuteScript(storage, commandArguments);
            case "exit" -> new Exit();
            case "remove_first" -> new RemoveFirst(storage);
            case "reorder" -> new Reorder(storage);
            case "sort" -> new Sort(storage);
            case "min_by_id" -> new MinById(storage);
            case "filter_by_transport" -> new FilterByTransport(storage, commandArguments);
            case "filter_contains_name" -> new FilterContainsName(storage, commandArguments);
            default -> System.out.println("Unknown command!");
        }
    }
}

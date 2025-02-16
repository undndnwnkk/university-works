package application;

import java.util.Arrays;
import commands.*;
import storage.Storage;

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

    private void getCommandAndArguments(String input) {
        input = input.trim();
        String[] commandAndArguments = input.split("\\s+");
        currentCommand = commandAndArguments[0];
        commandArguments = Arrays.copyOfRange(commandAndArguments, 1, commandAndArguments.length);
    }

    public CommandsList(Storage storage, String input) {
        getCommandAndArguments(input);

        switch (currentCommand) {
            case "help" -> new Help();
            case "info" -> new Info(storage);
            case "show" -> new Show(storage);
            case "add" -> new Add(storage, commandArguments);
            case "update" -> new Update(storage, commandArguments);
            case "remove_by_id" -> new RemoveById(storage, commandArguments);
//            case "clear" -> new Clear();
//            case "save" -> new Save();
//            case "execute_script" -> new ExecuteScript(commandArguments);
//            case "exit" -> new Exit();
//            case "remove_first" -> new RemoveFirst();
//            case "reorder" -> new Reorder();
//            case "sort" -> new Sort();
//            case "min_by_id" -> new MinById();
//            case "filter_by_transport" -> new FilterByTransport(commandArguments);
//            case "filter_contains_name" -> new FilterContainsName(commandArguments);
            default -> System.out.println("Unknown command!");
        }
    }
}
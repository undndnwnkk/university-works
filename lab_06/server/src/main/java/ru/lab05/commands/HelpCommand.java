package ru.lab05.commands;

/**
 * The Help class displays a list of available commands.
 */
public class HelpCommand {
    /**
     * Displays all available commands and their descriptions.
     */
    public HelpCommand() {
        System.out.println("Available commands:");
        System.out.println("help -> List available commands");
        System.out.println("show -> show current collection of classes");
        System.out.println("info -> Display collection information");
        System.out.println("add {element} -> Add a new element to the collection");
        System.out.println("update {id} {element} -> Update an element by its ID");
        System.out.println("remove_by_id {id} -> Remove an element by its ID");
        System.out.println("clear -> Clear the collection");
        System.out.println("save -> Save the collection to a file");
        System.out.println("execute_script {filename} -> Execute commands from a script");
        System.out.println("exit -> Exit the program");
        System.out.println("remove_first -> Remove the first element from the collection");
        System.out.println("reorder -> Reverse the order of the collection");
        System.out.println("sort -> Sort the collection");
        System.out.println("min_by_id -> Display the element with the smallest ID");
        System.out.println("filter_by_transport {transport} -> Display elements with the given transport type");
        System.out.println("filter_contains_name {name} -> Display elements containing the given name");
    }

    public static String execute() {
        return "\nAvailable commands:\n" +
                        "help -> List available commands\n" +
                        "show -> show current collection of classes\n" +
                        "info -> Display collection information\n" +
                        "add {element} -> Add a new element to the collection\n" +
                        "update {id} {element} -> Update an element by its ID\n" +
                        "remove_by_id {id} -> Remove an element by its ID\n" +
                        "clear -> Clear the collection\n" +
                        "save -> Save the collection to a file\n" +
                        "execute_script {filename} -> Execute commands from a script\n" +
                        "exit -> Exit the program\n" +
                        "remove_first -> Remove the first element from the collection\n" +
                        "reorder -> Reverse the order of the collection\n" +
                        "sort -> Sort the collection\n" +
                        "min_by_id -> Display the element with the smallest ID\n" +
                        "filter_by_transport {transport} -> Display elements with the given transport type\n" +
                        "filter_contains_name {name} -> Display elements containing the given name";
    }
}

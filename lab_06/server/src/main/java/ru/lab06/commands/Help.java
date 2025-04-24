package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;
import ru.lab06.storage.StorageLike;

/**
 * The Help class displays a list of available commands.
 */
public class Help implements Command {
    /**
     * Displays all available commands and their descriptions.
     */
    @Override
    public CommandResponse execute(StorageLike storage) {
        return new CommandResponse("Available commands:\n" +
                "help -> List available commands\n"
        + "info -> Display collection information\n"
        + "show -> Show current collection of classes\n"
        + "add {element} -> Add a new element to the collection\n"
        + "update {id} {element} -> Update an element by its ID\n"
        + "remove_by_id {id} -> Remove an element by its ID\n"
        + "clear -> Clear the collection\n"
        + "save -> Save the collection to a file\n"
        + "execute_script {filename} -> Execute commands from a script\n"
        + "exit -> Exit the program\n"
        + "remove_first -> Remove the first element from the collection\n"
        + "reorder -> Reverse the order of the collection\n"
        + "sort -> Sort the collection\n"
        + "min_by_id -> Display the element with the smallest ID\n"
        + "filter_by_transport {transport} -> Display elements with the given transport type\n"
        + "filter_contains_name {name} -> Display elements containing the given name");
    }
}

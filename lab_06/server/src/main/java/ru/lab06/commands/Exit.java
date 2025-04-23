package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.core.Storage;

/**
 * The Exit class terminates the program.
 */
public class Exit implements Command {
    /**
     * Prints a farewell message and exits the application.
     */

    @Override
    public CommandResponse execute(Storage storage) {
        System.exit(0);
        return new CommandResponse("Bye!");
    }
}

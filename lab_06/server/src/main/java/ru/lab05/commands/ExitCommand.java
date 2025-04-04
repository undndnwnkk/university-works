package ru.lab05.commands;

/**
 * The Exit class terminates the program.
 */
public class ExitCommand {
    /**
     * Prints a farewell message and exits the application.
     */
    public ExitCommand() {
        System.out.println("Bye!");
        System.exit(0);
    }
}

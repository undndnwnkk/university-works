package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.storage.StorageLike;
import ru.lab06.network.CommandExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExecuteScript implements Command {
    private final Object[] args;

    public ExecuteScript(Object[] args) {
        this.args = args;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        if (args.length < 1) {
            return new CommandResponse("Error: script filename was not provided");
        }

        String path = args[0].toString();
        File scriptFile = new File(path);
        if (!scriptFile.exists()) {
            return new CommandResponse("Error: file " + path + " does not exist");
        }

        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(scriptFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String commandName = parts[0];
                Object[] commandArgs = new Object[parts.length - 1];
                System.arraycopy(parts, 1, commandArgs, 0, commandArgs.length);

                Command command = CommandExecutor.createCommand(commandName, commandArgs);
                if (command == null) {
                    result.append("Unknown command: ").append(commandName).append("\n");
                    continue;
                }

                CommandResponse response = command.execute(storage);
                result.append(response.getMessage()).append("\n");
            }

        } catch (FileNotFoundException e) {
            return new CommandResponse("Script reading error: " + e.getMessage());
        }

        return new CommandResponse(result.toString());
    }
}

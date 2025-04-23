package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.command.CommandRequest;
import ru.lab06.network.CommandExecutor;
import ru.lab06.core.Storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class ExecuteScript implements Command {
    private final Object[] args;

    public ExecuteScript(Object[] args) {
        this.args = args;
    }

    @Override
    public CommandResponse execute(Storage storage) {
        if (args.length == 0) {
            return new CommandResponse("Error, incorrect path :(");
        }

        String filePath = args[0].toString();
        StringBuilder fullOutput = new StringBuilder("Script has started:\n");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String cmdName = parts[0];
                Object[] cmdArgs = new Object[parts.length - 1];
                System.arraycopy(parts, 1, cmdArgs, 0, parts.length - 1);

                CommandRequest requestFromFile = new CommandRequest(cmdName, cmdArgs);
                CommandResponse responseFromFile = CommandExecutor.execute(requestFromFile, storage);

                fullOutput.append("> ").append(line).append("\n");
                fullOutput.append(responseFromFile.getMessage()).append("\n");
            }
        } catch (Exception e) {
            return new CommandResponse("Error reading script: " + e.getMessage());
        }

        return new CommandResponse(fullOutput.toString());
    }
}

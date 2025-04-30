package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.model.Transport;
import ru.lab06.storage.StorageLike;

import java.util.Arrays;

public class FilterByTransport implements Command {
    private final Object[] commandArguments;

    public FilterByTransport(Object[] commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public CommandResponse execute(StorageLike storage) {
        if (commandArguments.length < 1) {
            return new CommandResponse("Please provide a transport value.");
        }

        String userInput = commandArguments[0].toString().toUpperCase();
        Transport[] validTransports = Transport.values();
        Transport transport = Arrays.stream(validTransports).filter(t -> t.name().equals(userInput)).findFirst().orElse(null);

        if (transport == null) {
            String options = String.join(", ",
                    Arrays.stream(validTransports)
                            .map(Enum::name)
                            .toArray(String[]::new)
            );
            return new CommandResponse("Invalid transport value. Available: " + options);
        }

        String result = storage.getFlatStorage().stream()
                .filter(flat -> transport.equals(flat.getTransport()))
                .map(Flat::toString)
                .reduce("", (a, b) -> a + b + "\n");

        if (result.isEmpty()) {
            return new CommandResponse("No flats with specified transport.");
        }

        return new CommandResponse(result.trim());
    }
}

package ru.lab06.commands;

import ru.lab06.command.Command;
import ru.lab06.command.CommandResponse;
import ru.lab06.model.Flat;
import ru.lab06.model.Transport;
import ru.lab06.storage.StorageLike;

import java.util.List;
import java.util.stream.Collectors;

public class FilterByTransport implements Command {
    private final Object[] args;

    public FilterByTransport(Object[] args) {
        this.args = args;
    }

    @Override
    public CommandResponse execute(StorageLike storage, String login) {
        if (args.length < 1 || !(args[0] instanceof String str)) {
            return new CommandResponse("Error: transport value is missing");
        }

        Transport transport;
        try {
            transport = Transport.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new CommandResponse("Error: unknown transport value");
        }

        List<Flat> result = storage.getFlatStorage().stream()
                .filter(flat -> flat.getTransport() == transport)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return new CommandResponse("No flats found with transport: " + transport);
        }

        StringBuilder out = new StringBuilder();
        for (Flat flat : result) {
            out.append("Flat ").append(flat.getId()).append(": ")
                    .append(flat.getName()).append("\n");
        }

        return new CommandResponse(out.toString());
    }
}

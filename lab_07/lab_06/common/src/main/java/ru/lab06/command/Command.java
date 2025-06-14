package ru.lab06.command;

import ru.lab06.command.CommandResponse;
import ru.lab06.storage.StorageLike;

public interface Command {
    CommandResponse execute(StorageLike storage, String login);
}

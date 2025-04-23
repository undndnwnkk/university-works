package ru.lab06.command;

import ru.lab06.core.Storage;

import java.io.Serializable;

@FunctionalInterface
public interface Command extends Serializable{
    CommandResponse execute(Storage storage);
}
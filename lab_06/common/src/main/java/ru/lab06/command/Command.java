package ru.lab06.command;

import ru.lab06.storage.StorageLike;

import java.io.Serializable;

@FunctionalInterface
public interface Command extends Serializable{
    CommandResponse execute(StorageLike storage);
}
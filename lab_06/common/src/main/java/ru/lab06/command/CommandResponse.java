package ru.lab06.command;

import java.io.Serializable;

public class CommandResponse implements Serializable {
    private final String message;

    public CommandResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
package ru.practicum.shareit.exceptions;

public class ErrorMessage {
    private final String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
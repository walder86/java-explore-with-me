package ru.practicum.base.exception;

public class DeniedAccess extends RuntimeException {
    public DeniedAccess(String message) {
        super(message);
    }
}

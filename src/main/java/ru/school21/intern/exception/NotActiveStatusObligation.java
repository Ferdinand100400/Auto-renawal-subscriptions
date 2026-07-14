package ru.school21.intern.exception;

public class NotActiveStatusObligation extends RuntimeException {

    private final String status;

    public NotActiveStatusObligation(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}

package ru.school21.intern.exception;

public class ThisCurrencyNotExistsException extends RuntimeException {

    private final String currency;

    public ThisCurrencyNotExistsException(String currency) {
        this.currency = currency;
    }

    public String currency() {
        return currency;
    }
}

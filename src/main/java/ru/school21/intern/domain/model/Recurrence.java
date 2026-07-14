package ru.school21.intern.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Recurrence {
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly"),
    NULL("Null");

    private final String value;

    Recurrence(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Recurrence fromValue(String value) {
        for (Recurrence recurrence : Recurrence.values()) {
            if (recurrence.value.equalsIgnoreCase(value)) {
                return recurrence;
            }
        }
        throw new IllegalArgumentException("Такой периодичности оплаты нет: " + value);
    }
}

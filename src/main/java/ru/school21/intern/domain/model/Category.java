package ru.school21.intern.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    SUBSCRIPTION("Subscription"),
    WARRANTY("Warranty"),
    BILL("Bill"),
    INSURANCE("Insurance");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Такой категории нет: " + value);
    }
}

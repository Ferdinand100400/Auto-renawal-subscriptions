package ru.school21.intern.exception;

import java.util.UUID;

public class NotFindObligationById extends RuntimeException {
    public NotFindObligationById(UUID id) {
        super("Не найдено обязательство по id: " + id);
    }
}

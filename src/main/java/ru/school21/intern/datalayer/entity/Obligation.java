package ru.school21.intern.datalayer.entity;

import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Obligation {

    private final UUID id;
    private final String title;
    private final BigDecimal amount;
    private final String currency;
    private final Category category;
    private final Recurrence recurrence;
    private Date nextPaymentDate;
    private Status status;
    private Timestamp createdAt, updatedAt;

    public Obligation(UUID id, String title, BigDecimal amount, String currency, Category category, Recurrence recurrence) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.recurrence = recurrence;
    }

    public Obligation(UUID id, String title, BigDecimal amount, String currency, Category category, Recurrence recurrence, Date nextPaymentDate, Status status, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.recurrence = recurrence;
        this.nextPaymentDate = nextPaymentDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }

    public Category category() {
        return category;
    }

    public Recurrence recurrence() {
        return recurrence;
    }

    public Date nextPaymentDate() {
        return nextPaymentDate;
    }

    public Status status() {
        return status;
    }

    public Timestamp createdAt() {
        return createdAt;
    }

    public Timestamp updatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package ru.school21.intern.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

public class ObligationDto {

    private UUID id;
    private final String title;
    private final BigDecimal amount;
    private final Currency currency;
    private final Category category;
    private final Recurrence recurrence;
    private Date nextPaymentDate;
    private Status status;
    private Timestamp createdAt, updatedAt;

    public ObligationDto(UUID id, String title, BigDecimal amount, Currency currency, Category category, Recurrence recurrence, Date nextPaymentDate, Status status, Timestamp createdAt, Timestamp updatedAt) {
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

    public ObligationDto(String title, BigDecimal amount, Currency currency, Category category, Recurrence recurrence, Date nextPaymentDate) {
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.recurrence = recurrence;
        this.nextPaymentDate = nextPaymentDate;
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

    public Currency currency() {
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

    public void updateNextPaymentDate(Date newDate) {
        if (newDate.before(nextPaymentDate))
            throw new IllegalArgumentException("Дата нового платежа не может быть до текущего");
        this.nextPaymentDate = newDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp createdAt() {
        return createdAt;
    }

    public Timestamp updatedAt() {
        return updatedAt;
    }
}

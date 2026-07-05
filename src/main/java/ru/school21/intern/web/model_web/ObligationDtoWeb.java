package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ObligationDtoWeb {
    private final UUID id;

    private final String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final BigDecimal amount;

    private final String currency;

    private final Category category;

    private final Recurrence recurrence;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("next_payment_date")
    private final Date nextPaymentDate;

    private final Status status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("create_at")
    private final Timestamp createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("update_at")
    private final Timestamp updatedAt;

    public ObligationDtoWeb(UUID id, String title, BigDecimal amount, String currency, Category category, Recurrence recurrence, Date nextPaymentDate, Status status, Timestamp createdAt, Timestamp updatedAt) {
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
}

package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class RenewalAlert {
    private final UUID id;
    private final String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("next_payment_date")
    private final Date nextPaymentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final BigDecimal amount;

    private final String currency;

    public RenewalAlert(UUID id, String title, Date nextPaymentDate, BigDecimal amount, String currency) {
        this.id = id;
        this.title = title;
        this.nextPaymentDate = nextPaymentDate;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Date nextPaymentDate() {
        return nextPaymentDate;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }
}

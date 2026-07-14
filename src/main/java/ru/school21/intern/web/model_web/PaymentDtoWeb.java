package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Currency;
import java.util.UUID;

public class PaymentDtoWeb {
    private final UUID id;
    private final UUID obligation_id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final BigDecimal amount;

    private final Currency currency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("paid_at")
    private final Timestamp paidAt;

    public PaymentDtoWeb(UUID id, UUID obligation_id, BigDecimal amount, Currency currency, Timestamp paidAt) {
        this.id = id;
        this.obligation_id = obligation_id;
        this.amount = amount;
        this.currency = currency;
        this.paidAt = paidAt;
    }

    public UUID id() {
        return id;
    }

    public UUID obligation_id() {
        return obligation_id;
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    public Timestamp paidAt() {
        return paidAt;
    }
}

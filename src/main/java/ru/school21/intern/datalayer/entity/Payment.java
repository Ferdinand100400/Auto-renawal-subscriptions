package ru.school21.intern.datalayer.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID obligation_id;
    private final BigDecimal amount;
    private final String currency;
    private final Timestamp paidAt;

    public Payment(UUID id, UUID obligation_id, BigDecimal amount, String currency, Timestamp paidAt) {
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

    public String currency() {
        return currency;
    }

    public Timestamp paidAt() {
        return paidAt;
    }
}

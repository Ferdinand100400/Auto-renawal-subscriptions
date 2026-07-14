package ru.school21.intern.domain.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

public class PaymentDto {
    private UUID id;
    private final UUID obligation_id;
    private final BigDecimal amount;
    private final Currency currency;
    private final Timestamp paidAt;

    public PaymentDto(UUID id, UUID obligation_id, BigDecimal amount, Currency currency, Timestamp paidAt) {
        this.id = id;
        this.obligation_id = obligation_id;
        this.amount = amount;
        this.currency = currency;
        this.paidAt = paidAt;
    }

    public PaymentDto(UUID obligation_id, BigDecimal amount, Currency currency) {
        this.obligation_id = obligation_id;
        this.amount = amount;
        this.currency = currency;
        this.paidAt = Timestamp.from(Instant.now());
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

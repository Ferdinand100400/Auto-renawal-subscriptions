package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.Recurrence;

import java.math.BigDecimal;
import java.util.Date;

public class ObligationRequest {

    private final String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final BigDecimal amount;

    private final String currency;

    private final Category category;

    private final Recurrence recurrence;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("next_payment_date")
    private final Date nextPaymentDate;

    public ObligationRequest(String title, BigDecimal amount, String currency, Category category, Recurrence recurrence, Date nextPaymentDate) {
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.recurrence = recurrence;
        this.nextPaymentDate = nextPaymentDate;
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
}

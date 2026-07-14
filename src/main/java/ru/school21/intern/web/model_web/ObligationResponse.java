package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObligationResponse {
    private final ObligationDtoWeb obligationDtoWeb;
    private String warning = null;
    private Map<String, BigDecimal> totals = null;
    private List<RenewalAlert> renewalAlerts = null;
    private PaymentDtoWeb paymentDtoWeb =  null;

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb) {
        this.obligationDtoWeb = obligationDtoWeb;
    }

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb, String warning) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.warning = warning;
    }

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb, Map<String, BigDecimal> totals, List<RenewalAlert> renewalAlerts) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.totals = totals;
        this.renewalAlerts = renewalAlerts;
    }

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb, PaymentDtoWeb paymentDtoWeb) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.paymentDtoWeb = paymentDtoWeb;
    }

    public ObligationDtoWeb obligationDtoWeb() {
        return obligationDtoWeb;
    }

    public String warning() {
        return warning;
    }

    public Map<String, BigDecimal> totals() {
        return totals;
    }

    public List<RenewalAlert> renewalAlerts() {
        return renewalAlerts;
    }

    public PaymentDtoWeb paymentDtoWeb() {
        return paymentDtoWeb;
    }
}

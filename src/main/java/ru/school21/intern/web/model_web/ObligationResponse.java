package ru.school21.intern.web.model_web;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObligationResponse {
    private final ObligationDtoWeb obligationDtoWeb;
    private final String warning;
    private final Totals totals;
    private final List<RenewalAlert> renewalAlerts;

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.warning = null;
    }

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb, String warning) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.warning = warning;
    }

    public ObligationResponse(ObligationDtoWeb obligationDtoWeb, Totals totals, List<RenewalAlert> renewalAlerts) {
        this.obligationDtoWeb = obligationDtoWeb;
        this.totals = totals;
        this.renewalAlerts = renewalAlerts;
    }

    public ObligationDtoWeb obligationDtoWeb() {
        return obligationDtoWeb;
    }

    public String warning() {
        return warning;
    }

    public Totals totals() {
        return totals;
    }

    public List<RenewalAlert> renewalAlerts() {
        return renewalAlerts;
    }
}

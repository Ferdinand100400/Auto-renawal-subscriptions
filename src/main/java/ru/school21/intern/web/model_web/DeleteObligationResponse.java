package ru.school21.intern.web.model_web;

import java.util.UUID;

public class DeleteObligationResponse {
    private final String type;
    private final UUID obligationId;

    public DeleteObligationResponse(String type, UUID obligationId) {
        this.type = type;
        this.obligationId = obligationId;
    }

    public String type() {
        return type;
    }

    public UUID obligationId() {
        return obligationId;
    }
}

package ru.school21.intern.domain.exception;

import ru.school21.intern.domain.model.ObligationDto;

public class ActiveObligationWithTitleAlreadyExists extends RuntimeException {

    private final ObligationDto obligationDto;

    public ActiveObligationWithTitleAlreadyExists(ObligationDto obligationDto) {
        this.obligationDto = obligationDto;
    }

    public ObligationDto obligationDto() {
        return obligationDto;
    }
}

package ru.school21.intern.web.mapper;

import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.web.model_web.ObligationDtoWeb;
import ru.school21.intern.web.model_web.ObligationRequest;

public class ObligationWebMapper {

    public static ObligationDtoWeb dtoToResponse(ObligationDto obligationDto) {
        return new ObligationDtoWeb(
                obligationDto.id(),
                obligationDto.title(),
                obligationDto.amount(),
                obligationDto.currency(),
                obligationDto.category(),
                obligationDto.recurrence(),
                obligationDto.nextPaymentDate(),
                obligationDto.status(),
                obligationDto.createdAt(),
                obligationDto.updatedAt()
        );
    }

    public static ObligationDto RequestToDto(ObligationRequest obligationRequest) {
        return new ObligationDto(
               obligationRequest.title(),
               obligationRequest.amount(),
               obligationRequest.currency(),
               obligationRequest.category(),
               obligationRequest.recurrence(),
               obligationRequest.nextPaymentDate()
        );
    }
}

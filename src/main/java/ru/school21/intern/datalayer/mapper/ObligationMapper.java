package ru.school21.intern.datalayer.mapper;

import ru.school21.intern.datalayer.entity.Obligation;
import ru.school21.intern.domain.model.ObligationDto;

import java.util.List;

public class ObligationMapper {

    public static Obligation dtoToEntity(ObligationDto obligationDto) {
        return new Obligation(obligationDto.id(),
                obligationDto.title(),
                obligationDto.amount(),
                obligationDto.currency(),
                obligationDto.category(),
                obligationDto.recurrence());
    }

    public static ObligationDto entityToDto(Obligation obligation) {
        return new ObligationDto(obligation.id(),
                obligation.title(),
                obligation.amount(),
                obligation.currency(),
                obligation.category(),
                obligation.recurrence(),
                obligation.nextPaymentDate(),
                obligation.createdAt(),
                obligation.updatedAt());
    }

    public static List<ObligationDto> listEntityToListDto(List<Obligation> obligations) {
        return obligations.stream()
                .map(ObligationMapper::entityToDto)
                .toList();
    }
}

package ru.school21.intern.web.mapper;

import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.PaymentDto;
import ru.school21.intern.exception.ThisCurrencyNotExistsException;
import ru.school21.intern.web.model_web.ObligationDtoWeb;
import ru.school21.intern.web.model_web.ObligationRequest;
import ru.school21.intern.web.model_web.PaymentDtoWeb;
import ru.school21.intern.web.model_web.RenewalAlert;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ObligationWebMapper {

    public static ObligationDtoWeb obligationDtoToResponse(ObligationDto obligationDto) {
        return new ObligationDtoWeb(
                obligationDto.id(),
                obligationDto.title(),
                obligationDto.amount(),
                obligationDto.currency().getCurrencyCode(),
                obligationDto.category(),
                obligationDto.recurrence(),
                obligationDto.nextPaymentDate(),
                obligationDto.status(),
                obligationDto.createdAt(),
                obligationDto.updatedAt()
        );
    }

    public static PaymentDtoWeb PaymentDtoToResponse(PaymentDto paymentDto) {
        return new PaymentDtoWeb(
                paymentDto.id(),
                paymentDto.obligation_id(),
                paymentDto.amount(),
                paymentDto.currency(),
                paymentDto.paidAt()
        );
    }

    public static ObligationDto RequestToDto(ObligationRequest obligationRequest) {
        try {
            return new ObligationDto(
                    obligationRequest.title(),
                    obligationRequest.amount(),
                    Currency.getInstance(obligationRequest.currency()),
                    obligationRequest.category(),
                    obligationRequest.recurrence(),
                    obligationRequest.nextPaymentDate()
            );
        } catch (IllegalArgumentException e) {
            throw new ThisCurrencyNotExistsException(obligationRequest.currency());
        }
    }

    public static List<RenewalAlert> listDtoToListRenewalAlert(List<ObligationDto> obligationsDto) {
        List<RenewalAlert> res = new ArrayList<>();
        for (ObligationDto obligationDto : obligationsDto) {
            res.add(dtoToRenewalAlert(obligationDto));
        }
        return res;
    }

    private static RenewalAlert dtoToRenewalAlert(ObligationDto obligationDto) {
        return new RenewalAlert(obligationDto.id(), obligationDto.title(), obligationDto.nextPaymentDate(), obligationDto.amount(), obligationDto.currency().getCurrencyCode());
    }
}

package ru.school21.intern.datalayer.mapper;

import ru.school21.intern.datalayer.entity.Payment;
import ru.school21.intern.domain.model.PaymentDto;

import java.util.List;

public class PaymentMapper {

    public static Payment dtoToEntity(PaymentDto paymentDto) {
        return new Payment(
              paymentDto.id(),
              paymentDto.obligation_id(),
              paymentDto.amount(),
              paymentDto.currency(),
              paymentDto.paidAt()
        );
    }

    public static List<PaymentDto> listEntityToListDto(List<Payment> payments) {
        return payments.stream()
                .map(p -> new PaymentDto(p.id(), p.obligation_id(), p.amount(), p.currency(), p.paidAt()))
                .toList();
    }
}

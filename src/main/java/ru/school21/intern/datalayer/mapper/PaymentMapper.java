package ru.school21.intern.datalayer.mapper;

import ru.school21.intern.datalayer.entity.Payment;
import ru.school21.intern.domain.model.PaymentDto;

import java.util.Currency;
import java.util.List;

public class PaymentMapper {

    public static Payment dtoToEntity(PaymentDto paymentDto) {
        return new Payment(
              paymentDto.id(),
              paymentDto.obligation_id(),
              paymentDto.amount(),
              paymentDto.currency().getCurrencyCode(),
              paymentDto.paidAt()
        );
    }

    public static List<PaymentDto> listEntityToListDto(List<Payment> payments) {
        return payments.stream()
                .map(p -> new PaymentDto(p.id(), p.obligation_id(), p.amount(), Currency.getInstance(p.currency()), p.paidAt()))
                .toList();
    }

    public static PaymentDto entityToDto(Payment payment) {
        return new PaymentDto(
                payment.id(),
                payment.obligation_id(),
                payment.amount(),
                Currency.getInstance(payment.currency()),
                payment.paidAt());
    }
}

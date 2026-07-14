package ru.school21.intern.domain.service;

import ru.school21.intern.datalayer.mapper.PaymentMapper;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.PaymentDto;
import ru.school21.intern.domain.repository.PaymentRepository;

import java.util.UUID;

public class PaymentService {

    private final PaymentRepository paymentRepo;

    public PaymentService(PaymentRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    public PaymentDto createPay(ObligationDto obligationDto) {
        PaymentDto paymentDto = new PaymentDto(obligationDto.id(), obligationDto.amount(), obligationDto.currency());
        return PaymentMapper.entityToDto(paymentRepo.createPayment(PaymentMapper.dtoToEntity(paymentDto)));
    }

    public void deleteByObligationId(UUID obligationId) {
        for (PaymentDto paymentDto : PaymentMapper.listEntityToListDto(paymentRepo.findAllByObligationId(obligationId))) {
            paymentRepo.deleteById(paymentDto.id());
        }
    }
}

package ru.school21.intern.domain.repository;

import org.springframework.stereotype.Repository;
import ru.school21.intern.datalayer.entity.Payment;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository {

    Payment createPayment(Payment payment);
    List<Payment> findAllByObligationId(UUID obligationId);
    void deleteById(UUID id);
}

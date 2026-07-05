package ru.school21.intern.web.controller;

import org.springframework.http.ResponseEntity;
import ru.school21.intern.domain.exception.ActiveObligationWithTitleAlreadyExists;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.domain.service.ObligationService;
import ru.school21.intern.domain.service.PaymentService;
import ru.school21.intern.web.mapper.ObligationWebMapper;
import ru.school21.intern.web.model_web.ObligationDtoWeb;
import ru.school21.intern.web.model_web.ObligationRequest;
import ru.school21.intern.web.model_web.ObligationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObligationControllerImpl implements ObligationController {

    private final ObligationService obligationService;
    private final PaymentService paymentService;

    public ObligationControllerImpl(ObligationService obligationService, PaymentService paymentService) {
        this.obligationService = obligationService;
        this.paymentService = paymentService;
    }

    @Override
    public ResponseEntity<?> createObligation(ObligationRequest obligationRequest) {
        try {
            ObligationDto obligationDto = obligationService.createNewObligation(ObligationWebMapper.RequestToDto(obligationRequest));
            return ResponseEntity.ok(new ObligationResponse(ObligationWebMapper.dtoToResponse(obligationDto)));
        } catch (ActiveObligationWithTitleAlreadyExists e) {
            return ResponseEntity.ok(new ObligationResponse(
                    ObligationWebMapper.dtoToResponse(e.obligationDto()),
                    "Активное обязательство с таким названием уже существует"
            ));
        }
    }

    @Override
    public ResponseEntity<?> getObligations(Category category, Status status) {
        List<ObligationResponse> obligations = new ArrayList<>();
        for (ObligationDto obligationDto : obligationService.getAllObligation(category, status)) {
            obligations.add(new ObligationResponse(ObligationWebMapper.dtoToResponse(obligationDto)));
        }
        return ResponseEntity.ok(obligations);
    }

    @Override
    public ResponseEntity<?> getObligationByNextPaymentToRange(int n) {
        return null;
    }

    @Override
    public ResponseEntity<?> pay(UUID id) {
        return null;
    }

    @Override
    public void cancelObligation(UUID id) {

    }

    @Override
    public ResponseEntity<?> deleteObligation(UUID id) {
        return null;
    }
}

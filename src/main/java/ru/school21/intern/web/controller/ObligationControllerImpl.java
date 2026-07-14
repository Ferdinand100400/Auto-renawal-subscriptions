package ru.school21.intern.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.school21.intern.exception.ActiveObligationWithTitleAlreadyExists;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.domain.service.ObligationService;
import ru.school21.intern.domain.service.PaymentService;
import ru.school21.intern.exception.NotActiveStatusObligation;
import ru.school21.intern.exception.NotFindObligationById;
import ru.school21.intern.exception.ThisCurrencyNotExistsException;
import ru.school21.intern.web.mapper.ObligationWebMapper;
import ru.school21.intern.web.model_web.DeleteObligationResponse;
import ru.school21.intern.web.model_web.ObligationRequest;
import ru.school21.intern.web.model_web.ObligationResponse;
import ru.school21.intern.web.model_web.PaymentDtoWeb;

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
            return ResponseEntity.ok(new ObligationResponse(ObligationWebMapper.obligationDtoToResponse(obligationDto)));
        } catch (ActiveObligationWithTitleAlreadyExists e) {
            return ResponseEntity.ok(new ObligationResponse(
                    ObligationWebMapper.obligationDtoToResponse(e.obligationDto()),
                    "Активное обязательство с таким названием уже существует"
            ));
        } catch (ThisCurrencyNotExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка: переданной валюты не существует");
        }
    }

    @Override
    public ResponseEntity<?> getObligations(Category category, Status status) {
        List<ObligationResponse> obligations = new ArrayList<>();
        for (ObligationDto obligationDto : obligationService.getAllObligation(category, status)) {
            obligations.add(new ObligationResponse(ObligationWebMapper.obligationDtoToResponse(obligationDto)));
        }
        return ResponseEntity.ok(obligations);
    }

    @Override
    public ResponseEntity<?> getObligationByNextPaymentToRange(Integer n) {
        List<ObligationResponse> response = new ArrayList<>();
        for (ObligationDto obligationDto : obligationService.getObligationByNextPaymentToRange(n)) {
            response.add(new ObligationResponse(
                    ObligationWebMapper.obligationDtoToResponse(obligationDto),
                    obligationService.calculateTotalAmountObligations(),
                    ObligationWebMapper.listDtoToListRenewalAlert(obligationService.getRenewalAlertByRange(n)))
            );
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> pay(UUID id) {
        try {
            obligationService.pay(id);
            var obligation = obligationService.pay(id);
            var createPayment = paymentService.createPay(obligation);
            PaymentDtoWeb payment = ObligationWebMapper.PaymentDtoToResponse(createPayment);
            return ResponseEntity.ok(new ObligationResponse(
                    ObligationWebMapper.obligationDtoToResponse(obligation),
                    payment
            ));
        } catch (NotFindObligationById e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (NotActiveStatusObligation e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Статус не активный: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> cancelObligation(UUID id) {
        try {
            obligationService.cancelObligation(id);
            return ResponseEntity.ok("Успешно отменено!");
        } catch (NotFindObligationById e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (NotActiveStatusObligation e) {
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Невозможно отменить со статусом: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteObligation(UUID id) {
        try {
            obligationService.deleteObligation(id);
            paymentService.deleteByObligationId(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new DeleteObligationResponse("obligation_deleted", id));
        } catch (NotFindObligationById e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}

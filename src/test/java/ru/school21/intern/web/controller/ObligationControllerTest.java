package ru.school21.intern.web.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.domain.service.ObligationService;
import ru.school21.intern.exception.ActiveObligationWithTitleAlreadyExists;
import ru.school21.intern.web.mapper.ObligationWebMapper;
import ru.school21.intern.web.model_web.ObligationDtoWeb;
import ru.school21.intern.web.model_web.ObligationRequest;
import ru.school21.intern.web.model_web.ObligationResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObligationControllerTest {

    @Mock
    private ObligationService obligationService;

    @InjectMocks
    private ObligationControllerImpl controller;

    private ObligationRequest request;
    private ObligationDto dto;
    private ObligationResponse response;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        request = new ObligationRequest(
                "Test Obligation",
                new BigDecimal(10_000),
                "RUB",
                Category.BILL,
                Recurrence.NULL,
                new Date()
        );

        dto = new ObligationDto(
                id,
                "Test Obligation",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.BILL,
                Recurrence.NULL,
                new Date(),
                Status.ACTIVE,
                null,
                null
        );

        response = new ObligationResponse(
                new ObligationDtoWeb(
                        id,
                        dto.title(),
                        dto.amount(),
                        dto.currency().getCurrencyCode(),
                        dto.category(),
                        dto.recurrence(),
                        dto.nextPaymentDate(),
                        dto.status(),
                        dto.createdAt(),
                        dto.updatedAt()
                )
        );
    }

    @Test
    @DisplayName("POST /obligations: успешное создание - 200 OK с обязательством")
    void shouldCreateObligationSuccessfully() {
        when(obligationService.createNewObligation(any(ObligationDto.class))).thenReturn(dto);

        ObligationDtoWeb webDto = new ObligationDtoWeb(
                id,
                "Test Obligation",
                new BigDecimal(10_000),
                "RUB",
                Category.BILL,
                Recurrence.NULL,
                new Date(),
                Status.ACTIVE,
                null,
                null
        );

        try (MockedStatic<ObligationWebMapper> mapperMock = mockStatic(ObligationWebMapper.class)) {
            mapperMock.when(() -> ObligationWebMapper.RequestToDto(request)).thenReturn(dto);
            mapperMock.when(() -> ObligationWebMapper.obligationDtoToResponse(dto))
                    .thenReturn(webDto);

            ResponseEntity<?> result = controller.createObligation(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isInstanceOf(ObligationResponse.class);
            ObligationResponse body = (ObligationResponse) result.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.obligationDtoWeb().id()).isEqualTo(id);
            assertThat(body.warning()).isNull();
        }
    }

    @Test
    @DisplayName("POST /obligations: дубль активного обязательства - 200 OK с warning")
    void shouldReturnWarningWhenActiveObligationWithSameTitleExists() {
        String warningMessage = "Активное обязательство с таким названием уже существует";

        ObligationDtoWeb webDto = new ObligationDtoWeb(
                id,
                "Test Obligation",
                new BigDecimal(10_000),
                "RUB",
                Category.BILL,
                Recurrence.NULL,
                new Date(),
                Status.ACTIVE,
                null,
                null
        );

        when(obligationService.createNewObligation(any(ObligationDto.class)))
                .thenThrow(new ActiveObligationWithTitleAlreadyExists(dto));

        try (MockedStatic<ObligationWebMapper> mapperMock = mockStatic(ObligationWebMapper.class)) {
            mapperMock.when(() -> ObligationWebMapper.RequestToDto(request)).thenReturn(dto);
            mapperMock.when(() -> ObligationWebMapper.obligationDtoToResponse(dto))
                    .thenReturn(webDto);
            mapperMock.when(() -> ObligationWebMapper.obligationDtoToResponse(eq(dto)))
                    .thenReturn(webDto);

            ResponseEntity<?> result = controller.createObligation(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isInstanceOf(ObligationResponse.class);
            ObligationResponse body = (ObligationResponse) result.getBody();
            Assertions.assertNotNull(body);
            assertThat(body.obligationDtoWeb().id()).isEqualTo(id);
            assertThat(body.warning()).isEqualTo(warningMessage);
        }
    }
}

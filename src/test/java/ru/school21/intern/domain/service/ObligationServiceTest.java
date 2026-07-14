package ru.school21.intern.domain.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.school21.intern.datalayer.entity.Obligation;
import ru.school21.intern.datalayer.mapper.ObligationMapper;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.domain.repository.ObligationRepository;
import ru.school21.intern.exception.ActiveObligationWithTitleAlreadyExists;
import ru.school21.intern.exception.NotActiveStatusObligation;
import ru.school21.intern.exception.NotFindObligationById;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObligationServiceTest {

    @Mock
    private ObligationRepository mockObligationRepo;

    @InjectMocks
    private ObligationService obligationService;

    private Date today;
    private Date yesterday;
    private Date tomorrow;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        today = new Date();
        yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000L);
        tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000L);
    }

    @Test
    @DisplayName("lazy expiry: должен обновить статус на EXPIRED для активного нерекуррентного обязательства с просроченной датой")
    void shouldExpireActiveNonRecurringObligationWithPastDate() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата кредита",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.BILL,
                Recurrence.NULL,
                yesterday,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findAll()).thenReturn(List.of(entity));
        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.listEntityToListDto(anyList()))
                    .thenReturn(List.of(dto));

            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            obligationService.getAllObligation(null, null);

            Assertions.assertEquals(Status.EXPIRED, dto.status());
            verify(mockObligationRepo, times(1)).save(any());
        }
    }

    @Test
    @DisplayName("lazy expiry: не должен обновлять статус для активного рекуррентного обязательства с просроченной датой")
    void shouldNotExpireActiveRecurringObligationWithPastDate() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата страховки авто",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.MONTHLY,
                yesterday,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findAll()).thenReturn(List.of(entity));
        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.listEntityToListDto(anyList()))
                    .thenReturn(List.of(dto));

            obligationService.getAllObligation(null, null);

            Assertions.assertEquals(Status.ACTIVE, dto.status());
            verify(mockObligationRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("lazy expiry: не должен обновлять статус для не активного обязательства")
    void shouldNotExpireInactiveObligation() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.MONTHLY,
                yesterday,
                Status.EXPIRED,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findAll()).thenReturn(List.of(entity));
        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.listEntityToListDto(anyList()))
                    .thenReturn(List.of(dto));

            obligationService.getAllObligation(null, null);

            Assertions.assertEquals(Status.EXPIRED, dto.status());
            verify(mockObligationRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("lazy expiry: не должен обновлять статус для обязательства с будущей датой платежа")
    void shouldNotExpireObligationWithFutureDate() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                tomorrow,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findAll()).thenReturn(List.of(entity));
        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.listEntityToListDto(anyList()))
                    .thenReturn(List.of(dto));

            obligationService.getAllObligation(null, null);

            Assertions.assertEquals(Status.ACTIVE, dto.status());
            verify(mockObligationRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("lazy expiry: должен обновить несколько обязательств за один вызов")
    void shouldExpireMultipleObligationsAtOnce() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        ObligationDto dto1 = new ObligationDto(
                id1,
                "Оплата 1",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                yesterday,
                Status.ACTIVE,
                null,
                null
        );

        ObligationDto dto2 = new ObligationDto(
                id2,
                "Оплата 2",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                yesterday,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity1 = new Obligation(
                id1,
                dto1.title(),
                dto1.amount(),
                dto1.currency().getCurrencyCode(),
                dto1.category(),
                dto1.recurrence()
        );

        Obligation entity2 = new Obligation(
                id2,
                dto2.title(),
                dto2.amount(),
                dto2.currency().getCurrencyCode(),
                dto2.category(),
                dto2.recurrence()
        );

        when(mockObligationRepo.findAll()).thenReturn(List.of(entity1, entity2));
        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.listEntityToListDto(anyList()))
                    .thenReturn(List.of(dto1, dto2));

            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity1, entity2);

            obligationService.getAllObligation(null, null);

            Assertions.assertEquals(Status.EXPIRED, dto1.status());
            Assertions.assertEquals(Status.EXPIRED, dto2.status());
            verify(mockObligationRepo, times(2)).save(any());
        }
    }

    @Test
    @DisplayName("pay(): для recurrence = NULL - статус CANCELLED, save() вызван")
    void shouldCancelObligationWhenRecurrenceIsNull() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                new Date(),
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            ObligationDto result = obligationService.pay(id);

            Assertions.assertEquals(Status.CANCELLED, dto.status());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("pay(): для recurrence = MONTHLY - nextPaymentDate + 1 месяц")
    void shouldAddOneMonthForMonthlyRecurrence() {
        Date currentDate = Date.from(LocalDate.of(2026, 7, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date expectedDate = Date.from(LocalDate.of(2026, 8, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.MONTHLY,
                currentDate,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            ObligationDto result = obligationService.pay(id);

            Assertions.assertEquals(expectedDate, dto.nextPaymentDate());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("pay(): для recurrence = QUARTERLY - nextPaymentDate + 3 месяца")
    void shouldAddThreeMonthsForQuarterlyRecurrence() {
        Date currentDate = Date.from(LocalDate.of(2026, 7, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date expectedDate = Date.from(LocalDate.of(2026, 10, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.QUARTERLY,
                currentDate,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            ObligationDto result = obligationService.pay(id);

            Assertions.assertEquals(expectedDate, dto.nextPaymentDate());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("pay(): для recurrence = YEARLY - nextPaymentDate + 1 год")
    void shouldAddOneYearForYearlyRecurrence() {
        Date currentDate = Date.from(LocalDate.of(2026, 7, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date expectedDate = Date.from(LocalDate.of(2027, 7, 15)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.YEARLY,
                currentDate,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            ObligationDto result = obligationService.pay(id);

            Assertions.assertEquals(expectedDate, dto.nextPaymentDate());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("pay(): граничный случай — оплата 31-го числа с recurrence = MONTHLY")
    void shouldHandleMonthEndDateForMonthlyRecurrence() {
        Date currentDate = Date.from(LocalDate.of(2026, 1, 31)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        // 31 января + 1 месяц = 28 февраля (не 31 февраля)
        Date expectedDate = Date.from(LocalDate.of(2026, 2, 28)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.MONTHLY,
                currentDate,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            ObligationDto result = obligationService.pay(id);

            Assertions.assertEquals(expectedDate, dto.nextPaymentDate());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("pay(): для не-active обязательства - выбрасывается NotActiveStatusObligation")
    void shouldThrowExceptionWhenObligationNotActive() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                new Date(),
                Status.CANCELLED,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);

            try {
                obligationService.pay(id);
                Assertions.fail();
            } catch (NotActiveStatusObligation e) {
                Assertions.assertTrue(true);
                verify(mockObligationRepo, never()).save(any());
            }
        }
    }

    @Test
    @DisplayName("pay(): обязательство не найдено - выбрасывается NotFindObligationById")
    void shouldThrowExceptionWhenObligationNotFound() {
        when(mockObligationRepo.findById(id)).thenReturn(Optional.empty());

        try {
            obligationService.pay(id);
            Assertions.fail();
        } catch (NotFindObligationById e) {
            Assertions.assertTrue(true);
            verify(mockObligationRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("cancelObligation(): активное обязательство - статус CANCELLED")
    void shouldCancelActiveObligation() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                new Date(),
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);

            obligationService.cancelObligation(id);

            Assertions.assertEquals(Status.CANCELLED, dto.status());
            verify(mockObligationRepo, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("cancelObligation(): не active обязательство - выбрасывается NotActiveStatusObligation")
    void shouldThrowExceptionWhenCancellingNotActiveObligation() {
        ObligationDto dto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                new Date(),
                Status.EXPIRED,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                dto.title(),
                dto.amount(),
                dto.currency().getCurrencyCode(),
                dto.category(),
                dto.recurrence()
        );

        when(mockObligationRepo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.entityToDto(entity)).thenReturn(dto);

            try {
                obligationService.cancelObligation(id);
                Assertions.fail();
            } catch (NotActiveStatusObligation e) {
                Assertions.assertTrue(true);
                verify(mockObligationRepo, never()).save(any());
            }
        }
    }

    @Test
    @DisplayName("cancelObligation(): обязательство не найдено - выбрасывается NotFindObligationById")
    void shouldThrowExceptionWhenCancellingNotFoundObligation() {
        when(mockObligationRepo.findById(id)).thenReturn(Optional.empty());

        try {
            obligationService.cancelObligation(id);
            Assertions.fail();
        } catch (NotFindObligationById e) {
            Assertions.assertTrue(true);
            verify(mockObligationRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("createNewObligation(): создание с будущей датой - статус ACTIVE")
    void shouldCreateObligationWithActiveStatusWhenDateIsFuture() {
        ObligationDto dto = new ObligationDto(
                null,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                tomorrow,
                null,
                null,
                null
        );

        ObligationDto savedDto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                tomorrow,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                savedDto.title(),
                savedDto.amount(),
                savedDto.currency().getCurrencyCode(),
                savedDto.category(),
                savedDto.recurrence()
        );

        when(mockObligationRepo.save(any(Obligation.class))).thenReturn(entity);
        when(mockObligationRepo.findActiveByTitle(anyString())).thenReturn(Optional.empty());

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);
            mapperMock.when(() -> ObligationMapper.entityToDto(entity))
                    .thenReturn(savedDto);

            ObligationDto result = obligationService.createNewObligation(dto);

            Assertions.assertEquals(Status.ACTIVE, dto.status());
            verify(mockObligationRepo, times(1)).save(any(Obligation.class));
        }
    }

    @Test
    @DisplayName("createNewObligation(): создание с прошлой датой - статус EXPIRED")
    void shouldCreateObligationWithExpiredStatusWhenDateIsPast() {
        ObligationDto dto = new ObligationDto(
                null,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                yesterday,
                null,
                null,
                null
        );

        ObligationDto savedDto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                yesterday,
                Status.EXPIRED,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                savedDto.title(),
                savedDto.amount(),
                savedDto.currency().getCurrencyCode(),
                savedDto.category(),
                savedDto.recurrence()
        );

        when(mockObligationRepo.save(any(Obligation.class))).thenReturn(entity);
        when(mockObligationRepo.findActiveByTitle(anyString())).thenReturn(Optional.empty());

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);
            mapperMock.when(() -> ObligationMapper.entityToDto(entity))
                    .thenReturn(savedDto);

            ObligationDto result = obligationService.createNewObligation(dto);

            Assertions.assertEquals(Status.EXPIRED, dto.status());
            verify(mockObligationRepo, times(1)).save(any(Obligation.class));
        }
    }

    @Test
    @DisplayName("createNewObligation(): дубль с активным обязательством  выбрасывается ActiveObligationWithTitleAlreadyExists")
    void shouldThrowExceptionWhenActiveObligationWithSameTitleExists() {
        String title = "Test Obligation";
        ObligationDto dto = new ObligationDto(
                null,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                tomorrow,
                null,
                null,
                null
        );

        ObligationDto savedDto = new ObligationDto(
                id,
                "Оплата",
                new BigDecimal(10_000),
                Currency.getInstance("RUB"),
                Category.INSURANCE,
                Recurrence.NULL,
                tomorrow,
                Status.ACTIVE,
                null,
                null
        );

        Obligation entity = new Obligation(
                id,
                savedDto.title(),
                savedDto.amount(),
                savedDto.currency().getCurrencyCode(),
                savedDto.category(),
                savedDto.recurrence()
        );

        when(mockObligationRepo.save(any(Obligation.class))).thenReturn(entity);

        // Находим дубль
        when(mockObligationRepo.findActiveByTitle(anyString())).thenReturn(Optional.of(entity));

        try (MockedStatic<ObligationMapper> mapperMock = mockStatic(ObligationMapper.class)) {
            mapperMock.when(() -> ObligationMapper.dtoToEntity(any(ObligationDto.class)))
                    .thenReturn(entity);
            mapperMock.when(() -> ObligationMapper.entityToDto(entity))
                    .thenReturn(savedDto);

            assertThatThrownBy(() -> obligationService.createNewObligation(dto))
                    .isInstanceOf(ActiveObligationWithTitleAlreadyExists.class)
                    .matches(e -> ((ActiveObligationWithTitleAlreadyExists) e).obligationDto().equals(savedDto));
            verify(mockObligationRepo, times(1)).save(any(Obligation.class));
        }
    }

}

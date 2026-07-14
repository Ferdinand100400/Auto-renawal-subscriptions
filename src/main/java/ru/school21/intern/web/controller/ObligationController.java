package ru.school21.intern.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.web.model_web.ObligationRequest;

import java.util.UUID;

@RestController
@RequestMapping("/obligations")
public interface ObligationController {

    @PostMapping
    @Operation(
            summary = "Создание нового обязательства",
            description = "Добавляется новое обязательство. Клиент передаёт все поля кроме id, status, created_at, updated_at"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обязательство успешно создано")
    })
    ResponseEntity<?> createObligation(@RequestBody ObligationRequest obligationRequest);

    @GetMapping
    @Operation(
            summary = "Возвращает список обязательств",
            description = "Принимает опциональные query-параметры category и status, " +
                    "поддерживает их одновременное применение и возвращает список обязательств. " +
                    "Результат отсортирован по next_payment_date по возрастанию."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обязательства получены")
    })
    ResponseEntity<?> getObligations(@RequestParam(value = "category", required = false) Category category, @RequestParam(value = "status", required = false) Status status);

    @GetMapping("/upcoming")
    @Operation(
            summary = "Возвращает обязательства с датой следующего платежа из диапазона",
            description = "Возвращает обязательства с датой следующего платежа в диапазоне [today, today + N days]. Параметр days — целое число, по умолчанию 7."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обязательства получены")
    })
    ResponseEntity<?> getObligationByNextPaymentToRange(@RequestParam(value = "n", required = false) Integer n);

    @PostMapping("/{id}/pay")
    @Operation(
            summary = "Фиксирует факт оплаты и обновляет обязательство",
            description = "Обновляет обязательство и фиксирует факт оплаты в таблице истории оплат"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Оплата успешно зафиксирована"),
            @ApiResponse(responseCode = "422", description = "Статус обязательства не активный")
    })
    ResponseEntity<?> pay(@PathVariable UUID id);

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Переводит обязательство в статус cancelled",
            description = "Переводит только активное обязательство в статус cancelled. Запись остаётся в базе — пользователь видит карточку со статусом cancelled."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обязательство успешно отменено"),
            @ApiResponse(responseCode = "422", description = "Статус обязательства не активный")
    })
    ResponseEntity<?> cancelObligation(@PathVariable UUID id);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаляет обязательство и все связанные с ним записи в таблице payments."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Обязательство успешно удалено")
    })
    ResponseEntity<?> deleteObligation(@PathVariable UUID id);

}

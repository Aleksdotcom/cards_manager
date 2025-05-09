package com.example.card_man.controllers;

import com.example.card_man.dtos.*;
import com.example.card_man.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/cards")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name="JWT Bearer")
public class CardController {
  private final CardService service;

  @GetMapping("/admin/generate-card-number")
  @Operation(
      description = "Для целей тестирования. Генерирует валидный номер банковской карты (16 цифр) с контрольной по алгоритму Луна.",
      summary = "Генерация номера кредитной карты",
      responses = {
          @ApiResponse(responseCode = "200", description = "Card Number",
              content = { @Content(
                  mediaType = "text/plain",
                  schema = @Schema(implementation = String.class) ) })
      }
  )
  public ResponseEntity<String> generate() {
    return ResponseEntity.ok(service.generate());
  }

  @PostMapping("/admin/create")
  @Operation(
      summary = "Регистрация Админом новой Карты для Юзера",
      description = """
          Номер карты валидируется по алгоритму Луна. Чтобы получить валидный номер (при тестировании),
          можно сгенерировать его с помощью эндпойнта 'cards/admin/generate-card-number'. Срок действия 
          новой карты должен быть не ранее текущей даты (карта не должна быть "просрочена"). 
          Баланс карты должен числом строго с двумя знаками после запятой.
          """
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Карта успешно создана",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CardResp.class))})
  })
  public ResponseEntity<CardResp> create(@Valid @RequestBody CardRequest dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
  }

  @GetMapping("/my-cards")
  @Operation(
      summary = "Получение Юзером списка принадлежащих ему карт (с фильтрацией и пагинацией)",
      description = "При отсутствии параметров фильтрации возвращается список ВСЕХ карт."
  )
  public ResponseEntity<PageResponse<CardResp>> getAllForMe(
      @Valid @ModelAttribute
      Optional<CardUserCriteriaReq> dto,
      Authentication auth,
      @ParameterObject
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
      Pageable pageable) {
    PageResponse<CardResp> fromDb = service.getAllUserCards(dto.orElse(new CardUserCriteriaReq()), auth, pageable);
    return ResponseEntity.ok(fromDb);
  }

  @GetMapping("/admin/list")
  @Operation(
      summary = "Получение Админом списка всех карт (с фильтрацией и пагинацией)",
      description = "При отсутствии параметров фильтрации возвращается список ВСЕХ карт."
  )
  public ResponseEntity<PageResponse<CardResp>> findByCriteria(
      @Valid @ModelAttribute
      Optional<CardCriteriaReq> dto,
      @ParameterObject
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(service.findByCriteria(dto.orElse(new CardCriteriaReq()), pageable));
  }

  @PatchMapping("/block/{id}")
  @Operation(
      summary = "Юзер помечает карту для блокировки Админом"
  )
  public  ResponseEntity<CardResp> blockRequest(@PathVariable Long id, Authentication auth) {
    return ResponseEntity.ok(service.blockRequest(id, auth));
  }

  @PatchMapping("/transfer")
  @Operation(
      summary = "Операция перевода Юзером средств между своими картами"
  )
  public  ResponseEntity<String> transfer(
      @Valid @RequestBody TransferReq dto,
      Authentication auth
  ) {
    return ResponseEntity.ok(service.transfer(dto, auth) ? "Success" : "Failure");
  }

  @DeleteMapping("/admin/delete/{id}")
  @Operation(
      summary = "Админ может удалить карту"
  )
  public ResponseEntity<?> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/admin/change-status/{id}")
  @Operation(
      summary = "Админ может установить статус карты"
  )
  public ResponseEntity<CardResp> changeStatus(
      @Valid @RequestBody CardStatusReq dto,
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(service.changeStatus(id, dto));
  }
}

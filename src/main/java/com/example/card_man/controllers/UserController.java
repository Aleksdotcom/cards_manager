package com.example.card_man.controllers;

import com.example.card_man.dtos.PageResponse;
import com.example.card_man.dtos.UserCriteriaReq;
import com.example.card_man.dtos.UserResp;
import com.example.card_man.dtos.UserUpdateReq;
import com.example.card_man.models.User;
import com.example.card_man.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Bearer")
public class UserController {
  private final UserService service;

  @GetMapping("/me")
  @Operation(
      summary = "Получение данных текущего Юзера (для тестирования)"
  )
  public ResponseEntity<UserResp> authenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User currentUser = (User) authentication.getPrincipal();
    UserResp dto = UserResp.toDto(currentUser);

    return ResponseEntity.ok(dto);
  }

  @GetMapping("/admin/list")
  @Operation(
      summary = "Получение Админом списка Юзеров (с фильтрацией и пагинацией)",
      description = "При отсутствии параметров фильтрации возвращается список ВСЕХ Юзеров."
  )
  public ResponseEntity<PageResponse<UserResp>> allUsers(
      @Valid @ModelAttribute
      Optional<UserCriteriaReq> dto,
      @ParameterObject
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
      Pageable pageable
  ) {
    PageResponse<UserResp> users = service.findByCriteria(dto.orElse(new UserCriteriaReq()), pageable);

    return ResponseEntity.ok(users);
  }

  @PatchMapping("/admin/update/{id}")
  @Operation(
      summary = "Админ может изменить данные Юзера"
  )
  public ResponseEntity<?> update(
      @PathVariable Long id, @Valid @RequestBody UserUpdateReq dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }

  @DeleteMapping("/admin/delete/{id}")
  @Operation(
      summary = "Админ может удалить данные Юзера"
  )
  public ResponseEntity<?> delete(
      @PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

}

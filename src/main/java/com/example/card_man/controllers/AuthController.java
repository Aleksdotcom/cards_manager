package com.example.card_man.controllers;

import com.example.card_man.dtos.AuthRequest;
import com.example.card_man.dtos.AuthResponse;
import com.example.card_man.dtos.UserRegisterReq;
import com.example.card_man.dtos.UserResp;
import com.example.card_man.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
  private final AuthService service;

  @PostMapping("/login")
  @Operation(
      description = "Get user ID and JWT token",
      summary = "Login with email and password",
      responses = {
          @ApiResponse(responseCode = "200", description = "Login",
              content = { @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AuthResponse.class) ) })
      }
  )
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest){
    AuthResponse authResponse = service.login(authRequest);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/sign-up")
  @Operation(
      summary = "Создаётся новый Юзер",
      description = """
          Роль нового юзера по умолчанию: 'USER'. Имя и фамилия автоматически используются 
          в поле CARDHOLDER для карты, поэтому должны содержать только заглавные латинские буквы.
          """)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User added successfully",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserResp.class))}),
  })
  public ResponseEntity<?> register(@Valid @RequestBody UserRegisterReq dto){
    UserResp regUser = service.register(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(regUser);
  }
}

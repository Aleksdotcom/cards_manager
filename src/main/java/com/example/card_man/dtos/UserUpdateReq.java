package com.example.card_man.dtos;

import com.example.card_man.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateReq {
  @Email
  @Schema(
      description = "email",
      example = "email@email.com",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  private String email;

  @Schema(
      description = "First Name to print it on card",
      example = "TOM",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp="[A-Z]+", message = "Cardholder name must contain only uppercase latin letters")
  @Size(min=2, max=20)
  private String firstName;

  @Schema(
      description = "First Name to print it on card",
      example = "SMITH",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp="[A-Z]+", message = "Cardholder last name must contain only uppercase latin letters")
  @Size(min=2, max=20)
  private String lastName;

  @Schema(
      description = "User role",
      example = "USER/ADMIN",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  private User.Role role;
}

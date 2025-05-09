package com.example.card_man.dtos;

import com.example.card_man.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCriteriaReq {
  @Schema(
      description = "User ID",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Min(1)
  private Long id;
  @Schema(
      description = "User email",
      example = "email@email.com",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Email
  private String email;
  @Schema(
      description = "User role",
      example = "USER/ADMIN",
      implementation = User.Role.class,
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  private User.Role role;
  @Schema(
      description = "User's CARDHOLDER first name",
      example = "TOM",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp="[A-Z]+", message = "Cardholder name must contain only uppercase latin letters")
  private String firstName;
  @Schema(
      description = "User's CARDHOLDER last name",
      example = "JOHNSON",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp="[A-Z]+", message = "Cardholder last name must contain only uppercase latin letters")
  private String lastName;
}

package com.example.card_man.dtos;

import com.example.card_man.models.CreditCard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardCriteriaReq {
  @Schema(
      description = "Cardholder User ID",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Min(1)
  private Long userId;

  @Schema(
      description = "Cardholder name to compare in LIKE statement",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp="[A-Z]+", message = "Cardholder string must contain only uppercase latin letters")
  private String cardHolder;

  @Schema(
      description = "Card status",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  private CreditCard.CardStatus status;

  @Schema(
      description = "User's request to block the Card",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  private Boolean toBlock;

  @Schema(
      description = "Card expiry date MM/yy",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$")
  private String expiryDate;
}

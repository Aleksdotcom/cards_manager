package com.example.card_man.dtos;

import com.example.card_man.utils.validators.TwoDecimalPlaces;
import com.example.card_man.utils.validators.ValidExpirationDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class CardRequest {
  @Positive
  @NotNull
  private Long userId;

  @CreditCardNumber
  @NotNull
  private String cardNumber;

  @NotNull
  @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiry date must be in MM/yy format")
  @ValidExpirationDate
  private String expiryDate;

  @NotNull
  @TwoDecimalPlaces
  private BigDecimal balance;

  @Schema(hidden = true)
  public BigInteger getBalanceAsBigInteger() {
    return balance.multiply(BigDecimal.valueOf(100)).toBigIntegerExact();
  }
}

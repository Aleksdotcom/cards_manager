package com.example.card_man.dtos;

import com.example.card_man.utils.validators.TwoDecimalPlaces;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class TransferReq {
  @NotNull
  @Min(1)
  private Long fromId;

  @NotNull
  @Min(1)
  private Long toId;

  @TwoDecimalPlaces
  @NotNull
  private BigDecimal amount;
  @Schema(hidden = true)
  public BigInteger getAmountAsBigInteger() {
    return amount.multiply(BigDecimal.valueOf(100)).toBigIntegerExact();
  }
}

package com.example.card_man.dtos;

import com.example.card_man.models.CreditCard;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardStatusReq {
  @NotNull
  private CreditCard.CardStatus status;
}

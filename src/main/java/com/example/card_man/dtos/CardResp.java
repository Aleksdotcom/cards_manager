package com.example.card_man.dtos;

import com.example.card_man.models.CreditCard;
import com.example.card_man.utils.Mask;
import com.example.card_man.utils.CardUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardResp {
  private Long id;
  private String cardHolder;
  @Mask
  private String cardNumber;
  private String expiryDate;
  private CreditCard.CardStatus status;
  private Boolean toBlock;
  private BigDecimal balance;
  private Long userId;

  /**
   * Преобразует сумму в копейках/центах в десятичное представление (рубли/доллары) с двумя знаками после запятой.
   *
   * @param amountInCents сумма в самых мелких единицах (например, 123456 = 1234.56)
   * @return BigDecimal с двумя знаками после запятой
   */
  public static BigDecimal fromCentsToDecimal(BigInteger amountInCents) {
    if (amountInCents == null) {
      return null;
    }
    return new BigDecimal(amountInCents).movePointLeft(2).setScale(2, RoundingMode.UNNECESSARY);
  }

  public static CardResp toDto(CreditCard card) {
    CardResp dto = new CardResp();
    dto.id = card.getId();
    dto.cardHolder = card.getCardHolder();
    dto.cardNumber = card.getCardNumber();
    dto.expiryDate = CardUtil.formatExpiryDate(card.getExpiryDate());
    dto.status = card.getStatus();
    dto.toBlock = card.getToBlock();
    dto.balance = fromCentsToDecimal(card.getBalance());
    dto.userId = card.getUserId();

    return dto;
  }

  @Override
  public String toString() {
    return "CardResp{" +
        "id=" + id +
        ", cardHolder='" + cardHolder + '\'' +
        ", cardNumber='" + CardUtil.mask(cardNumber) + '\'' +
        ", expiryDate='" + expiryDate + '\'' +
        ", status=" + status +
        ", toBlock=" + toBlock +
        ", balance=" + balance +
        ", userId=" + userId +
        '}';
  }
}

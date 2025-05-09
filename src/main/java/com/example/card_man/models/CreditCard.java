package com.example.card_man.models;

import com.example.card_man.utils.CreditCardEncryptor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {
  @Id
  @GeneratedValue
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String cardHolder;

  @Convert(converter = CreditCardEncryptor.class)
  @Column(nullable = false, unique = true)
  private String cardNumber;

  @Column(nullable = false)
  private LocalDate expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private CardStatus status = CardStatus.ACTIVE;

  @Column(nullable = false)
  @Builder.Default
  private Boolean toBlock = false;

  @Column(nullable = false)
  private BigInteger balance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User owner;

  @Column(name = "user_id", insertable = false, updatable = false)
  private Long userId;

  public enum CardStatus {
    ACTIVE,
    BLOCKED,
    EXPIRED
  }
}

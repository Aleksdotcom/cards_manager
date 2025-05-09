package com.example.card_man.services;

import com.example.card_man.dtos.*;
import com.example.card_man.exceptions.ConflictException;
import com.example.card_man.models.CreditCard;
import com.example.card_man.models.User;
import com.example.card_man.repositories.CardRepository;
import com.example.card_man.utils.CardUtil;
import com.example.card_man.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
  private final CardRepository repository;
  private final UserService userService;

  public PageResponse<CardResp> getAllUserCards(CardUserCriteriaReq dto, Authentication auth, Pageable pageable) {
    Long userId = extractUserId(auth);
    LocalDate date = null;
    if (dto.getExpiryDate() != null) {
      date = CardUtil.parseExpiryDate(dto.getExpiryDate());
    }
    Page<CreditCard> fromDb = repository.findByCriteria(
        userId,
        null,
        dto.getStatus(),
        dto.getToBlock(),
        date,
        pageable
    );
    Page<CardResp> responses = fromDb.map(CardResp::toDto);
    return new PageResponse<>(responses);
  }

  @Transactional
  public  CardResp create(CardRequest dto) {
    User user = userService.findOne(dto.getUserId());

    if (repository.existsByCardNumber(dto.getCardNumber())) {
      throw new ConflictException("ERROR: Card already registered: " + dto.getCardNumber());
    }

    CreditCard newCard = CreditCard.builder()
        .owner(user)
        .cardHolder(user.getFirstName() + " " + user.getLastName())
        .cardNumber(dto.getCardNumber())
        .expiryDate(CardUtil.parseExpiryDate(dto.getExpiryDate()))
        .balance(dto.getBalanceAsBigInteger())
        .build();

    try {
      repository.saveAndFlush(newCard);
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(e.getMostSpecificCause().getMessage());
    }

    return CardResp.toDto(newCard);
  }

  public PageResponse<CardResp> findByCriteria(
      CardCriteriaReq dto,
      Pageable pageable
      ) {
    LocalDate date = null;
    if (dto.getExpiryDate() != null) {
      date = CardUtil.parseExpiryDate(dto.getExpiryDate());
    }
    String cardHolder = dto.getCardHolder();
    String cardHolderPattern = cardHolder != null ? "%" + cardHolder + "%" : null;

    Page<CreditCard> cards = repository.findByCriteria(
        dto.getUserId(),
        cardHolderPattern,
        dto.getStatus(),
        dto.getToBlock(),
        date,
        pageable
    );

    Page<CardResp> cardResponses = cards.map(CardResp::toDto);

    return new PageResponse<>(cardResponses);
  }

  public String generate() {
    return CardUtil.generateCardNumber();
  }

  @Transactional
  public CardResp blockRequest(Long cardId, Authentication auth) {
    Long userId = extractUserId(auth);
    CreditCard card = findOne(cardId);
    if (userId != card.getUserId()) {
      throw new IllegalArgumentException("You can not block Card #" + cardId);
    }
    card.setToBlock(true);
    repository.saveAndFlush(card);
    return CardResp.toDto(card);
  }

  public CreditCard findOne(Long cardId) {
    return  repository.findById(cardId).orElseThrow(
        () -> new NoSuchElementException("There is no Card with id: " + cardId)
    );
  }

  private Long extractUserId( Authentication auth){
    User user = null;
    if (auth instanceof UsernamePasswordAuthenticationToken token) {
      user = (User) token.getPrincipal();
    }
    assert user != null;
    return user.getId();
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public boolean transfer(TransferReq dto, Authentication auth) {
    List<CreditCard> list = new ArrayList<>(2);

    Long userId = extractUserId(auth);
    CreditCard fromCard = findOne(dto.getFromId());
    CreditCard toCard = findOne(dto.getToId());
    if (userId != fromCard.getUserId() || userId != toCard.getUserId()) {
      throw new IllegalArgumentException(
          "You can not transfer from Card #" +
              fromCard.getId() +
          " to Card #" + toCard.getId());
    }
    BigInteger amount = dto.getAmountAsBigInteger();
    BigInteger fromBalance = fromCard.getBalance();
    BigInteger toBalance =toCard.getBalance();
    int result = amount.compareTo(fromBalance);
    if (result > 0) {
      throw new IllegalArgumentException("Insufficient funds");
    }
    BigInteger newFrom = fromBalance.add(amount.negate());
    BigInteger newTo = toBalance.add(amount);

    fromCard.setBalance(newFrom);
    toCard.setBalance(newTo);
    list.add(fromCard);
    list.add(toCard);

    repository.saveAllAndFlush(list);
    return true;
  }

  @Transactional
  public void delete(Long id) {
    CreditCard found = findOne(id);
    repository.delete(found);
  }

  @Transactional
  public CardResp changeStatus(Long id, CardStatusReq dto) {
    CreditCard found = findOne(id);
    found.setStatus(dto.getStatus());
    return CardResp.toDto(repository.save(found));
  }
}

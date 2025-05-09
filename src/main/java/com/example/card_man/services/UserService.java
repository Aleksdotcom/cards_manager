package com.example.card_man.services;

import com.example.card_man.dtos.PageResponse;
import com.example.card_man.dtos.UserCriteriaReq;
import com.example.card_man.dtos.UserResp;
import com.example.card_man.dtos.UserUpdateReq;
import com.example.card_man.exceptions.ConflictException;
import com.example.card_man.models.User;
import com.example.card_man.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;
  public PageResponse<UserResp> allUsers(Pageable pageable) {

    Page<User> fromDb = repository.findAll(pageable);
    Page<UserResp> result = fromDb.map(UserResp::toDto);

    return new PageResponse<>(result);
  }

  public PageResponse<UserResp> findByCriteria(UserCriteriaReq dto, Pageable pageable) {
    String firstName = dto.getFirstName();
    String firstNamePattern = firstName != null ? "%" + firstName + "%" : null;
    String lastName = dto.getLastName();
    String lasNamePattern = lastName != null ? "%" + lastName + "%" : null;

    Page<User> users = repository.findByCriteria(
        dto.getId(), dto.getEmail(), dto.getRole(), firstNamePattern, lasNamePattern, pageable
    );
    Page<UserResp> userRespPage = users.map(UserResp::toDto);

    return  new PageResponse<>(userRespPage);
  }

  public User findOne(Long userId) {
    return repository.findById(userId).orElseThrow(
        () -> new NoSuchElementException("There is no User with id: " + userId)
    );
  }

  public UserResp update(Long id, UserUpdateReq dto) {

    User fromDb = findOne(id);

    String email = dto.getEmail();
    if (email != null && !email.equals(fromDb.getEmail()) ) {
      if (repository.existsByEmail(email)) {
        throw new ConflictException("ERROR: Email already registered:" + dto.getEmail());
      }
      fromDb.setEmail(email);
    }

    if (dto.getFirstName() != null) {
      fromDb.setFirstName(dto.getFirstName());
    }

    if (dto.getLastName() != null) {
      fromDb.setLastName(dto.getLastName());
    }

    if (dto.getRole() != null) {
      fromDb.setRole(dto.getRole());
    }

    return UserResp.toDto(repository.save(fromDb));
  }

  public void delete(Long id) {
    User fromDb = findOne(id);
    repository.delete(fromDb);
  }
}

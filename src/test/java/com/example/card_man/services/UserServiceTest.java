package com.example.card_man.services;

import com.example.card_man.dtos.PageResponse;
import com.example.card_man.dtos.UserCriteriaReq;
import com.example.card_man.dtos.UserResp;
import com.example.card_man.dtos.UserUpdateReq;
import com.example.card_man.exceptions.ConflictException;
import com.example.card_man.models.CreditCard;
import com.example.card_man.models.User;
import com.example.card_man.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService service;

  @Test
  void testFindByCriteria() {
    UserCriteriaReq criteria = new UserCriteriaReq();
    criteria.setFirstName("John");
    criteria.setLastName("Doe");

    Pageable pageable = PageRequest.of(0, 10);
    User user = User.builder()
        .id(1L)
        .email("john@example.com")
        .password("password")
        .firstName("John")
        .lastName("Doe")
        .role(User.Role.USER)
        .cards(List.of(new CreditCard()))
        .build();
    Page<User> page = new PageImpl<>(List.of(user));

    when(repository.findByCriteria(
        isNull(),
        isNull(),
        isNull(),
        eq("%John%"),
        eq("%Doe%"),
        eq(pageable)
    )).thenReturn(page);

    PageResponse<UserResp> response = service.findByCriteria(criteria, pageable);

    assertNotNull(response);
    assertEquals(1, response.getContent().size());
    assertEquals("John", response.getContent().get(0).getFirstName());
  }

  @Test
  void testFindOne_UserExists() {
    User user = User.builder()
        .id(1L)
        .email("john@example.com")
        .password("password")
        .firstName("John")
        .lastName("Doe")
        .role(User.Role.USER)
        .cards(List.of(new CreditCard()))
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(user));

    User found = service.findOne(1L);

    assertNotNull(found);
    assertEquals("john@example.com", found.getEmail());
  }

  @Test
  void testFindOne_UserNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.findOne(1L));
    assertEquals("There is no User with id: 1", ex.getMessage());
  }

  @Test
  void testUpdate_Success() {
    User existing = User.builder()
        .id(1L)
        .email("old@example.com")
        .password("password")
        .firstName("Old")
        .lastName("Name")
        .role(User.Role.USER)
        .cards(List.of(new CreditCard()))
        .build();
    UserUpdateReq updateReq = new UserUpdateReq();
    updateReq.setEmail("new@example.com");
    updateReq.setFirstName("New");
    updateReq.setLastName("User");
    updateReq.setRole(User.Role.ADMIN);

    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.existsByEmail("new@example.com")).thenReturn(false);
    when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    UserResp updated = service.update(1L, updateReq);

    assertNotNull(updated);
    assertEquals("new@example.com", updated.getEmail());
    assertEquals("New", updated.getFirstName());
    assertEquals("User", updated.getLastName());
    assertEquals(User.Role.ADMIN, updated.getRole());
  }

  @Test
  void testUpdate_EmailConflict() {
    User existing = User.builder()
        .id(1L)
        .email("old@example.com")
        .password("password")
        .firstName("Old")
        .lastName("Name")
        .role(User.Role.USER)
        .cards(List.of(new CreditCard()))
        .build();
    UserUpdateReq updateReq = new UserUpdateReq();
    updateReq.setEmail("existing@example.com");

    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.existsByEmail("existing@example.com")).thenReturn(true);

    ConflictException ex = assertThrows(ConflictException.class, () -> service.update(1L, updateReq));
    assertEquals("ERROR: Email already registered: existing@example.com", ex.getMessage());
  }

  @Test
  void testDelete_Success() {
    User user = User.builder()
        .id(1L)
        .email("john@example.com")
        .password("password")
        .firstName("John")
        .lastName("Doe")
        .role(User.Role.USER)
        .cards(List.of(new CreditCard()))
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(user));

    service.delete(1L);

    verify(repository).delete(user);
  }
}
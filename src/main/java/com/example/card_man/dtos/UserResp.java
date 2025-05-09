package com.example.card_man.dtos;

import com.example.card_man.models.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResp {
  private Long id;
  private String email;
  private User.Role role;
  private String firstName;
  private String lastName;

  public static UserResp toDto(User user) {
    UserResp dto = new UserResp();
    dto.id = user.getId();
    dto.email = user.getEmail();
    dto.role = user.getRole();
    dto.firstName = user.getFirstName();
    dto.lastName = user.getLastName();
    return dto;
  }
}

package com.example.card_man.repositories;

import com.example.card_man.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE" +
        "(:id IS NULL OR u.id = :id) AND" +
        "(:email IS NULL OR u.email = :email) AND" +
        "(:role IS NULL OR u.role = :role) AND" +
        "(:firstName IS NULL OR u.firstName LIKE :firstName) AND" +
        "(:lastName IS NULL OR u.lastName LIKE :lastName)"
    )
    Page<User> findByCriteria(
        @Param("id") Long id,
        @Param("email") String email,
        @Param("role") User.Role role,
        @Param("firstName") String firstName,
        @Param("lastName") String lastName,
        Pageable pageable
    );
}

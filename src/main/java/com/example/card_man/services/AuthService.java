package com.example.card_man.services;

import com.example.card_man.dtos.AuthRequest;
import com.example.card_man.dtos.AuthResponse;
import com.example.card_man.dtos.UserRegisterReq;
import com.example.card_man.dtos.UserResp;
import com.example.card_man.exceptions.ConflictException;
import com.example.card_man.models.User;
import com.example.card_man.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResp register(UserRegisterReq dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("ERROR: Email already registered:" + dto.getEmail());
        }
        User newUser = User.builder()
            .email(dto.getEmail())
            .password(encoder.encode(dto.getPassword()))
            .role(User.Role.USER)
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .build();
        repository.saveAndFlush(newUser);
        return UserResp.toDto(newUser);
    }

    public AuthResponse login(AuthRequest authRequest){
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            ));
        User user = (User) auth.getPrincipal();
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), token);
    }
}

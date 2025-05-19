package com.example.card_man.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthenticationEntryPoint point;
  private final CustomAccessDeniedHandler handler;

  @Value("${app.cors.origins}")
  private String[] corsOrigins;
  @Value("${app.cors.methods}")
  private String[] corsMethods;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors((cors) -> cors
            .configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers(
                    "/auth/**",
                    "/swagger-ui.html", "/swagger-ui/**",
                    "/swagger-resources", "/swagger-resources/**",
                    "/configuration/ui", "/configuration/security",
                    "/v3/api-docs", "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/users/admin/**", "/cards/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**", "/cards/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(ex ->
            ex.authenticationEntryPoint(point).accessDeniedHandler(handler))
        .build();
  }

  UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(corsOrigins));
    configuration.setAllowedMethods(Arrays.asList(corsMethods));
    configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

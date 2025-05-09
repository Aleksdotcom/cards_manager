package com.example.card_man.controllers;

import com.example.card_man.dtos.ErrorResp;
import com.example.card_man.exceptions.ConflictException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExControllerAdvice {
  @ExceptionHandler({
      BadCredentialsException.class,
      AccountStatusException.class,
      AccessDeniedException.class,
      UsernameNotFoundException.class,
  })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ProblemDetail handleSecurityException(RuntimeException ex) {
    ProblemDetail errorDetail = null;

    if (ex instanceof BadCredentialsException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
      errorDetail.setProperty("description", "The username or password is incorrect");
    }
    if (ex instanceof AccountStatusException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
      errorDetail.setProperty("description", "The account is locked");
    }
    if (ex instanceof AccessDeniedException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
      errorDetail.setProperty("description", "You are not authorized to access this resource");
    }
    if (ex instanceof UsernameNotFoundException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
      errorDetail.setProperty("description", "The username is incorrect");
    }

    return errorDetail;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ErrorResp> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    ErrorResp errorResponse = new ErrorResp(
        HttpStatus.FORBIDDEN.value(),
        "Forbidden resource",
        ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ProblemDetail handleConflictException(ConflictException ex) {
    ProblemDetail resp = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), ex.getMessage());
    resp.setProperty("description", "Conflict");
    return resp;
  }

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFoundException(NoSuchElementException ex) {
    ProblemDetail resp = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    resp.setProperty("description", "Entity not found");
    return resp;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResp> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

    Map<String, String> responseBody = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      responseBody.put(fieldName, errorMessage);
    });

    ErrorResp errorResponse = new ErrorResp(
        HttpStatus.BAD_REQUEST.value(),
        "Validation failed",
        "Validation error(s)",
        request.getRequestURI()
    );

    responseBody.forEach((field, message) -> errorResponse.setProperty(field, message));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResp> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
    Map<String, String> error = new HashMap<>();
    Throwable cause = ex.getCause();

    if (cause instanceof InvalidFormatException formatException) {
      String fieldName = formatException.getPath().stream()
          .map(ref -> ref.getFieldName())
          .findFirst()
          .orElse("unknown");

      error.put(fieldName, "Invalid format: expected type " + formatException.getTargetType().getSimpleName());
    } else {
      error.put("error", "Malformed JSON request or data type mismatch");
    }

    ErrorResp errorResponse = new ErrorResp(
        HttpStatus.BAD_REQUEST.value(),
        "JSON error",
        ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage(),
        request.getRequestURI()
    );

    error.forEach((field, message) -> errorResponse.setProperty(field, message));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResp> handleHibernateErrors(
      InvalidDataAccessApiUsageException ex,
      HttpServletRequest request
  ) {
    ErrorResp errorResponse = new ErrorResp(
        HttpStatus.BAD_REQUEST.value(),
        "Invalid Data Access",
        ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}

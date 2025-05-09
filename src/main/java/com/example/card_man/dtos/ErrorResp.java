package com.example.card_man.dtos;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResp {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;

  // Дополнительные произвольные поля
  private Map<String, Object> properties = new HashMap<>();

  public ErrorResp(int status, String error, String message, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }

  public void setProperty(String field, Object value) {
    properties.put(field, value);
  }
}

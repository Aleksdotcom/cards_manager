package com.example.card_man.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CreditCardEncryptor implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return CryptoUtils.encrypt(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return CryptoUtils.decrypt(dbData);
  }
}

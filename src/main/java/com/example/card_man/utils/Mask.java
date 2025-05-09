package com.example.card_man.utils;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JacksonAnnotationsInside
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JsonSerialize(using = MaskSerializer.class)
public @interface Mask {
}

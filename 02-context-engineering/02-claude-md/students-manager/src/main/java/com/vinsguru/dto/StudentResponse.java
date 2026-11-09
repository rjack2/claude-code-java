package com.vinsguru.dto;

public record StudentResponse(
        Long id,
        String name,
        Integer age,
        String className
) {
}

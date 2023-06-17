package com.hevlar.konney.presentation;

public record ErrorDto(
        String objectName,
        String fieldName,
        String rejectedValue,
        String errorMessage
) {}

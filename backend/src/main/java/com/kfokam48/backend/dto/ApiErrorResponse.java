package com.kfokam48.backend.dto;

public record ApiErrorResponse(
        String code,
        String message
) {
}
package com.example.flowstate.dto.response;

public record ErrorResponse(
        int status,
        String message
) {}

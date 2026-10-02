package com.Romito.ecommerce_api.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {}
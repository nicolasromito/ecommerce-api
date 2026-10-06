package com.Romito.ecommerce_api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 8, fraction = 2, message = "El precio no puede tener más de 8 dígitos enteros ni más de 2 decimales") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotNull Long categoryId
) {}
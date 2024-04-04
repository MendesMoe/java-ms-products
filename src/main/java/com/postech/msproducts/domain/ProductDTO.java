package com.postech.msproducts.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProductDTO(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Min(value=0)
        Double price,

        @NotNull
        @Min(value=0)
        int quantity_stk,

        LocalDateTime created_at,

        LocalDateTime updated_at
) {
}

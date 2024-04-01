package com.postech.msproducts.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDTO(
        @NotBlank
        String name,

        @NotNull
        @Min(value=0)
        Double price,

        @NotNull
        @Min(value=0)
        int quantity_stk
) {
}

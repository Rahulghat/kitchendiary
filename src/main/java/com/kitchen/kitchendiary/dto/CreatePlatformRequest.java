package com.kitchen.kitchendiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlatformRequest(
        @NotBlank @Size(max = 20) String code,   // ZOMATO, SWIGGY, DIRECT
        @NotBlank @Size(max = 80) String name
) {}
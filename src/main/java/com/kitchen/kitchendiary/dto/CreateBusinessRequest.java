package com.kitchen.kitchendiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBusinessRequest(
    @NotBlank @Size(max = 160) String name,
    @Size(max = 15) String gstin,
    String address,
    String city,
    String state) {}

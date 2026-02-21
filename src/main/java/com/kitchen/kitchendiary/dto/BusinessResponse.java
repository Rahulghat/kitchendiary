package com.kitchen.kitchendiary.dto;

import java.time.Instant;

public record BusinessResponse(
        Long id,
        Long ownerUserId,
        String name,
        String gstin,
        String address,
        String city,
        String state,
        Instant createdAt
) {}

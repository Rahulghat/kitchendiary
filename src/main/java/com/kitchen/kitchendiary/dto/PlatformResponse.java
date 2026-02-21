package com.kitchen.kitchendiary.dto;

import java.time.Instant;

public record PlatformResponse(
    Long id, Long businessId, String code, String name, Instant createdAt) {}

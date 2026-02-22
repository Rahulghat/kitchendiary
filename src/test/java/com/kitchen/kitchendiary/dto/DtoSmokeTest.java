package com.kitchen.kitchendiary.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class DtoSmokeTest {

  @Test
  void businessAndPlatformResponse_shouldExposeRecordFields() {
    Instant now = Instant.parse("2026-02-22T00:00:00Z");
    BusinessResponse business =
        new BusinessResponse(1L, 2L, "Kitchen", "GST", "Addr", "Pune", "MH", now);
    PlatformResponse platform = new PlatformResponse(3L, 1L, "SWG", "Swiggy", now);

    assertEquals("Kitchen", business.name());
    assertEquals("SWG", platform.code());
  }
}

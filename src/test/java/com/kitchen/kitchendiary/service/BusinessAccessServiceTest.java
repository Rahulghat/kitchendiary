package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.repositories.BusinessRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessAccessServiceTest {

  @Mock private BusinessRepository businessRepository;
  @InjectMocks private BusinessAccessService service;

  @Test
  void getBusinessOrThrow_shouldReturnBusiness() {
    Business business = new Business();
    when(businessRepository.findByIdAndOwnerId(10L, 2L)).thenReturn(Optional.of(business));

    assertSame(business, service.getBusinessOrThrow(2L, 10L));
  }

  @Test
  void getBusinessOrThrow_shouldThrowWhenNotFound() {
    when(businessRepository.findByIdAndOwnerId(10L, 2L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> service.getBusinessOrThrow(2L, 10L));
  }
}


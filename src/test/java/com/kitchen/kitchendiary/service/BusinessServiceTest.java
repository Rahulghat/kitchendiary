package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.User;
import com.kitchen.kitchendiary.repositories.BusinessRepository;
import com.kitchen.kitchendiary.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

  @Mock private BusinessRepository businessRepository;
  @Mock private UserRepository userRepository;
  @InjectMocks private BusinessService service;

  @Test
  void create_shouldCreateBusinessForUser() {
    User owner = new User();
    owner.setId(5L);
    when(userRepository.findById(5L)).thenReturn(Optional.of(owner));

    when(businessRepository.save(org.mockito.ArgumentMatchers.any(Business.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CreateBusinessRequest req =
        new CreateBusinessRequest("Kitchen A", "27ABCDE1234F1Z5", "Addr", "Mumbai", "MH");
    Business created = service.create(5L, req);

    assertEquals("Kitchen A", created.getName());
    assertEquals("Mumbai", created.getCity());
    assertSame(owner, created.getOwner());
  }

  @Test
  void create_shouldThrowWhenUserMissing() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> service.create(1L, new CreateBusinessRequest("X", null, null, null, null)));
  }

  @Test
  void list_shouldReturnOwnerBusinesses() {
    List<Business> expected = List.of(new Business(), new Business());
    when(businessRepository.findAllByOwnerIdOrderByCreatedAtDesc(7L)).thenReturn(expected);

    assertSame(expected, service.list(7L));
  }

  @Test
  void get_shouldThrowWhenDenied() {
    when(businessRepository.findByIdAndOwnerId(9L, 3L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> service.get(3L, 9L));
  }
}


package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.PlatformRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlatformServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private PlatformRepository platformRepository;
  @InjectMocks private PlatformService service;

  @Test
  void create_shouldNormalizeCodeAndSave() {
    Business business = new Business();
    business.setId(11L);
    when(businessAccessService.getBusinessOrThrow(2L, 11L)).thenReturn(business);
    when(platformRepository.existsByBusinessIdAndCode(11L, "SWIGGY")).thenReturn(false);
    when(platformRepository.save(org.mockito.ArgumentMatchers.any(Platform.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Platform platform = service.create(2L, 11L, new CreatePlatformRequest(" swiggy ", "Swiggy"));

    assertEquals("SWIGGY", platform.getCode());
    assertEquals("Swiggy", platform.getName());
    assertSame(business, platform.getBusiness());
  }

  @Test
  void create_shouldThrowOnDuplicateCode() {
    Business business = new Business();
    when(businessAccessService.getBusinessOrThrow(2L, 11L)).thenReturn(business);
    when(platformRepository.existsByBusinessIdAndCode(11L, "SWIGGY")).thenReturn(true);

    assertThrows(
        IllegalArgumentException.class,
        () -> service.create(2L, 11L, new CreatePlatformRequest("swiggy", "Swiggy")));
  }

  @Test
  void list_shouldCheckAccessAndReturnPlatforms() {
    List<Platform> expected = List.of(new Platform());
    when(platformRepository.findAllByBusinessId(11L)).thenReturn(expected);

    assertSame(expected, service.list(2L, 11L));
  }
}


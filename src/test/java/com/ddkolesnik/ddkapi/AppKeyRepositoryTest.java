package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.model.app.AppKey;
import com.ddkolesnik.ddkapi.repository.AppKeyRepository;
import com.ddkolesnik.ddkapi.service.AppKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Alexandr Stegnin
 */

@SpringBootTest
public class AppKeyRepositoryTest {

    @Mock
    AppKeyRepository appKeyRepository;

    @InjectMocks
    AppKeyService appKeyService;

    private static AppKey appKey;

    private static final String key = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        appKey = new AppKey();
        appKey.setKey(key);
    }

    @Test
    @DisplayName("Получаем значение по ключу")
    public void existsByKey() {
        when(appKeyRepository.existsByKey(key)).thenReturn(true);
        boolean exists = appKeyService.existByKey(appKey.getKey());
        assertTrue(exists);
        verify(appKeyRepository, times(1)).existsByKey(key);
    }
}

package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.model.app.AppToken;
import com.ddkolesnik.ddkapi.repository.app.AppTokenRepository;
import com.ddkolesnik.ddkapi.service.app.AppTokenService;
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
public class AppTokenRepositoryTest {

    @Mock
    AppTokenRepository appTokenRepository;

    @InjectMocks
    AppTokenService appTokenService;

    private static AppToken appToken;

    private static final String key = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        appToken = new AppToken();
        appToken.setToken(key);
    }

    @Test
    @DisplayName("Получаем значение по ключу")
    public void existsByKey() {
        when(appTokenRepository.existsByToken(key)).thenReturn(true);
        boolean exists = appTokenService.existByToken(appToken.getToken());
        assertTrue(exists);
        verify(appTokenRepository, times(1)).existsByToken(key);
    }
}

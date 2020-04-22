package com.ddkolesnik.ddkapi.service.app;

import com.ddkolesnik.ddkapi.repository.app.AppTokenRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppTokenService {

    AppTokenRepository appTokenRepository;

    public AppTokenService(AppTokenRepository appTokenRepository) {
        this.appTokenRepository = appTokenRepository;
    }

    public boolean existByToken(String token) {
        return appTokenRepository.existsByToken(token);
    }

}

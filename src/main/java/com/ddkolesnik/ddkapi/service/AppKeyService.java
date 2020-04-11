package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.repository.AppKeyRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppKeyService {

    AppKeyRepository appKeyRepository;

    public AppKeyService(AppKeyRepository appKeyRepository) {
        this.appKeyRepository = appKeyRepository;
    }

    public boolean existByKey(String key) {
        return appKeyRepository.existsByKey(key);
    }

}

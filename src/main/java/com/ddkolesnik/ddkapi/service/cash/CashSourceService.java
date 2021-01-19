package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.repository.cash.CashSourceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CashSourceService {

    CashSourceRepository cashSourceRepository;

    public CashSource findByOrganization(String organization) {
        CashSource cashSource = cashSourceRepository.findByOrganization(organization);
        if (cashSource == null) {
            String message = String.format("Источник денег [%s] не найден.", organization);
            log.error(message);
            throw new ApiException(message, HttpStatus.NOT_FOUND);
        }
        return cashSource;
    }

}

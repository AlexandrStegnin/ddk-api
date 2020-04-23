package com.ddkolesnik.ddkapi.service.cash;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.cash.CashSource;
import com.ddkolesnik.ddkapi.repository.cash.CashSourceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CashSourceService {

    CashSourceRepository cashSourceRepository;

    public CashSource findByOrganization(String organization) {
        CashSource cashSource = cashSourceRepository.findByOrganization(organization);
        if (Objects.isNull(cashSource)) {
            throw new ApiException("Источник денег [" + organization + "] не найден.", HttpStatus.NOT_FOUND);
        }
        return cashSource;
    }

}

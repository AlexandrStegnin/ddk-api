package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.money.Facility;
import com.ddkolesnik.ddkapi.repository.money.FacilityRepository;
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
public class FacilityService {

    FacilityRepository facilityRepository;

    public Facility findByFullName(String fullName) {
        Facility facility = facilityRepository.findByFullNameEqualsIgnoreCase(fullName);
        if (Objects.isNull(facility)) {
            throw new ApiException("Объект с названием = [" + fullName + "] не найден", HttpStatus.NOT_FOUND);
        }
        return facility;
    }

}

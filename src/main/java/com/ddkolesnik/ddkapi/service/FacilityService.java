package com.ddkolesnik.ddkapi.service;

import com.ddkolesnik.ddkapi.configuration.exception.ApiException;
import com.ddkolesnik.ddkapi.model.Facility;
import com.ddkolesnik.ddkapi.repository.FacilityRepository;
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

    public Facility findByName(String name) {
        Facility facility = facilityRepository.findByName(name);
        if (Objects.isNull(facility)) {
            throw new ApiException("Объект с названием = [" + name + "] не найден", HttpStatus.NOT_FOUND);
        }
        return facility;
    }

}

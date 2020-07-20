package com.ddkolesnik.ddkapi.service.money;

import com.ddkolesnik.ddkapi.model.money.UnderFacility;
import com.ddkolesnik.ddkapi.repository.money.UnderFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional
public class UnderFacilityService {

    private final UnderFacilityRepository underFacilityRepository;

    public UnderFacilityService(UnderFacilityRepository underFacilityRepository) {
        this.underFacilityRepository = underFacilityRepository;
    }

    public UnderFacility findByName(String name) {
        return underFacilityRepository.findByName(name);
    }

}

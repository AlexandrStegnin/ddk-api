package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.UnderFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UnderFacilityRepository extends JpaRepository<UnderFacility, Long> {

    UnderFacility findByName(String name);

}

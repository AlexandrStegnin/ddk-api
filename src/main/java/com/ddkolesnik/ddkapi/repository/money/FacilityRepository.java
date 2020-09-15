package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    Facility findByFullNameEqualsIgnoreCase(String fullName);

    Facility findByProjectUUID(String projectUUID);

}

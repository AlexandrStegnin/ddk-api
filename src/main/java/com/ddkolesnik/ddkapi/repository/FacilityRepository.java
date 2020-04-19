package com.ddkolesnik.ddkapi.repository;

import com.ddkolesnik.ddkapi.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    Facility findByName(String name);

}

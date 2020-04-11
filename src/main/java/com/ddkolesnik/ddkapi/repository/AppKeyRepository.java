package com.ddkolesnik.ddkapi.repository;

import com.ddkolesnik.ddkapi.model.app.AppKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AppKeyRepository extends JpaRepository<AppKey, Long> {

    Boolean existsByKey(String key);

}

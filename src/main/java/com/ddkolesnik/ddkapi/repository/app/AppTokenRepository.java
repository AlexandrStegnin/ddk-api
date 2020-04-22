package com.ddkolesnik.ddkapi.repository.app;

import com.ddkolesnik.ddkapi.model.app.AppToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AppTokenRepository extends JpaRepository<AppToken, Long> {

    Boolean existsByToken(String token);

}

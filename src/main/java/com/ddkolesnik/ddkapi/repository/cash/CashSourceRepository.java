package com.ddkolesnik.ddkapi.repository.cash;

import com.ddkolesnik.ddkapi.model.cash.CashSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface CashSourceRepository extends JpaRepository<CashSource, Long> {

    CashSource findByOrganization(String organization);

}

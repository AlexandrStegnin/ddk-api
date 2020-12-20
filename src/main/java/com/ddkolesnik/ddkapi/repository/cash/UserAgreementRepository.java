package com.ddkolesnik.ddkapi.repository.cash;

import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {
}

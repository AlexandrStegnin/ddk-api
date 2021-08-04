package com.ddkolesnik.ddkapi.repository.cash;

import com.ddkolesnik.ddkapi.model.cash.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexandr Stegnin
 */

@Repository
@Transactional
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long> {

    UserAgreement findByFacilityIdAndConcludedFrom(Long facilityId, Long investorId);

}

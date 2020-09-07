package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface MoneyRepository extends JpaRepository<Money, Long>, JpaSpecificationExecutor<Money> {

    Money findByTransactionUUID(String uuid);

    Long countByInvestorIdAndDateClosingIsNull(Long investorId);

    void deleteByTransactionUUID(String transactionUUID);
}

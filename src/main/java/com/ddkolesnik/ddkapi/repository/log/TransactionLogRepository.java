package com.ddkolesnik.ddkapi.repository.log;

import com.ddkolesnik.ddkapi.model.log.TransactionLog;
import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import com.ddkolesnik.ddkapi.model.money.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    List<TransactionLog> findByInvestorsCashesContains(Money cash);

    List<TransactionLog> findByBlockedFromId(Long blockedFromId);

    List<TransactionLog> findByAccountTransactionsContains(AccountTransaction accountTransaction);

}

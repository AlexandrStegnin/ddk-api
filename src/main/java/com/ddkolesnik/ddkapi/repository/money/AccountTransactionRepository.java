package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */
@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

  AccountTransaction findByParentId(Long parentTxId);

  List<AccountTransaction> findByTransactionUUID(String uuid);

  boolean existsByTransactionUUID(String uuid);

}

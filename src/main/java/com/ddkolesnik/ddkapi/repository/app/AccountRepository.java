package com.ddkolesnik.ddkapi.repository.app;

import com.ddkolesnik.ddkapi.model.app.Account;
import com.ddkolesnik.ddkapi.util.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    Account findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);

}

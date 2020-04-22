package com.ddkolesnik.ddkapi.repository.money;

import com.ddkolesnik.ddkapi.model.money.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    Investor findByLogin(String login);

}
